package ru.otus.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "client")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client implements Cloneable {

    @Id
    @EqualsAndHashCode.Include
    //    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // , generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("this-escape")
    public Client(String name, Address address, List<Phone> phones) {
        this.name = name;
        this.address = address;
        this.phones = phones;
        initPhonesClient();
    }

    @SuppressWarnings("this-escape")
    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
        initPhonesClient();
    }

    private void initPhonesClient() {
        if (this.phones != null) {
            this.phones.forEach(p -> p.setClient(this));
        }
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address clonedAddress =
                this.address != null ? new Address(this.address.getId(), this.address.getStreet()) : null;

        List<Phone> clonedPhones = this.phones != null
                ? this.phones.stream()
                        .map(p -> new Phone(p.getId(), p.getNumber()))
                        .toList()
                : null;

        return new Client(this.id, this.name, clonedAddress, clonedPhones);
    }
}
