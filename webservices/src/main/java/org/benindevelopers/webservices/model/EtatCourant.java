package org.benindevelopers.webservices.model;

/**
 * @author Seth-Pharès Gnavo (sethgnavo)
 */
public class EtatCourant {
    private String description;
    private boolean etat;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }
}
