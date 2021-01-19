package Pacchi;

import Sism.Sistema;

import java.util.ArrayList;

public class GestorePacchi implements GestorePacchiInterface {
    Sistema sistema = Sistema.getInstance();
    ArrayList<Pacco> lPacchi;
    //TODO Aggiungere le variabili.

    @Override
    public boolean addPacco(int idCommerciante, int idCliente) {
        return creaPacco(idCommerciante,idCliente);
    }

    @Override
    public boolean removePacco(int idPacco) {
        //TODO correggere o non elimina pacchi dal "cloud".
        Pacco p = getPacco(idPacco);
        if (lPacchi.contains(p)) {
            lPacchi.remove(p);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Pacco getPacco(int idPacco) {
        //TODO
        //come GestoreCorse.getCorsa | Riccardo
        return null;
    }

    @Override
    public int getNewId() {
        return sistema.generaNuovoId();
    }

    @Override
    public boolean savePacco(ArrayList<? extends PaccoInterface> listaPacchi) {
        return sistema.savePacco(listaPacchi);
    }
/*  todo: probabilmente non serve, valutare! -ale
    @Override
    public boolean setPacco(int idPacco, int idCommerciante, int idCliente) {
        Pacco p = getPacco(idPacco);
        return p.setCommerciante(idCommerciante) && p.setCliente(idCliente);
    }

 */
}
