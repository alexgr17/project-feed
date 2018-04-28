package ru.alexgryaznov.flclient.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.alexgryaznov.flclient.dao.ClientRepository;
import ru.alexgryaznov.flclient.domain.Client;

@RestController
@RequestMapping("client")
public class ClientController {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Client> getClients() {
        return clientRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createClient(@RequestBody Client client) {
        clientRepository.save(client);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void removeClient(@RequestParam int clientId) {
        clientRepository.deleteById(clientId);
    }
}
