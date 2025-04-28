package tn.esprit.entities;

public class MagasinStatDTO {
    private int magasinId;
    private String nomMagasin;
    private double chiffreAffaire;

    public MagasinStatDTO(int magasinId, String nomMagasin, double chiffreAffaire) {
        this.magasinId = magasinId;
        this.nomMagasin = nomMagasin;
        this.chiffreAffaire = chiffreAffaire;
    }

    // Getters
    public int getMagasinId() {
        return magasinId;
    }

    public String getNomMagasin() {
        return nomMagasin;
    }

    public double getChiffreAffaire() {
        return chiffreAffaire;
    }
}