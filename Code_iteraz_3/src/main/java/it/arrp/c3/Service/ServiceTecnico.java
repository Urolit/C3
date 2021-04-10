package it.arrp.c3.Service;

import it.arrp.c3.Model.Admin;
import it.arrp.c3.Model.Cliente;
import it.arrp.c3.Model.Repository.TecnicoRepository;
import it.arrp.c3.Model.Tecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Classe che si occupa di effettuare le operazioni riguardanti la classe Tecnico.
 */
@Service
public class ServiceTecnico {
    @Autowired
    ServiceCliente serviceCliente;
    @Autowired
    TecnicoRepository repoTecnico;

    public String getChiaveTecnico(Long idTecnico){
        //TODO probabilmente da eliminare, se non ricordo male doveva servire a prendere una chiave per aprire
        // i box, che poi abbiamo essenzialmente sostituito con il codice identificativo --Ric
        return null;
    }

    public boolean creaTecnico(Long idCliente, Admin admin) {
        serviceCliente.aggiungiRuoloTecnico(idCliente);
        repoTecnico.save(new Tecnico(idCliente,admin));
        //TODO da controllare, mi sembra vada bene ora. --Ric
        return true;
    }
}
