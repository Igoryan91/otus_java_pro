package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.dao.InMemoryUserDao;
import ru.otus.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.mapper.AddressDtoMapper;
import ru.otus.mapper.ClientDtoMapper;
import ru.otus.mapper.PhoneDtoMapper;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.DataTemplateHibernate;
import ru.otus.repository.HibernateUtils;
import ru.otus.server.ClientsWebServer;
import ru.otus.server.ClientsWebServerImpl;
import ru.otus.services.DbServiceClientImpl;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;
import ru.otus.sessionmanager.TransactionManagerHibernate;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница клиентов
    http://localhost:8080/clients

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) throws Exception {
        var userDao = new InMemoryUserDao();

        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var clientDtoMapper = new ClientDtoMapper(new AddressDtoMapper(), new PhoneDtoMapper());

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, clientDtoMapper);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(userDao);

        ClientsWebServer clientsWebServer =
                new ClientsWebServerImpl(WEB_SERVER_PORT, authService, dbServiceClient, gson, templateProcessor);

        clientsWebServer.start();
        clientsWebServer.join();
    }
}
