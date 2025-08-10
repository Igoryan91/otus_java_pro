package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.dto.ClientDto;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.services.DBServiceClient;
import ru.otus.services.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONES = "phones";

    private final transient DBServiceClient dbServiceClient;
    private final transient TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        List<ClientDto> clients = dbServiceClient.findAll();
        paramsMap.put(TEMPLATE_ATTR_CLIENTS, clients);
        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter(NAME);
        String street = request.getParameter(ADDRESS);
        String[] numbers = request.getParameterValues(PHONES);

        Address address = new Address(street);
        List<Phone> phones = Arrays.stream(numbers).map(Phone::new).toList();
        Client client = new Client(name, address, phones);

        dbServiceClient.saveClient(client);

        response.sendRedirect(TEMPLATE_ATTR_CLIENTS);
    }
}
