package tn.esprit.PIDEV.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class chatBotRequest {
    // Static data for demonstration purposes
    private String model;
    private List<Message> messages;

    public String getAllOffresAsTable() {
        // Static data for demonstration purposes
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| ID         | Date de Creation       | Description                             | Intitule            | Nombre de Places | User ID    |\n");
        stringBuilder.append("|------------|------------------------|-----------------------------------------|---------------------|------------------|------------|\n");
        stringBuilder.append("| 1          | 2024-04-18             | Offre de stage en développement web     | Stage Développeur   | 5                | 1234       |\n");
        stringBuilder.append("| 2          | 2024-04-19             | Offre d'emploi en ingénierie logicielle | Ingénieur Logiciel  | 3                | 5678       |\n");
        return stringBuilder.toString();
    }

    public chatBotRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("system",
                "Utilisez cet ensemble de données fourni des offres de stages et d'emplois :" + getAllOffresAsTable() +
                        "\n Répondez aux demandes de l'utilisateur uniquement en fonction de ce jeu de données." +
                        "Lorsque vous répondez aux questions, assurez-vous que les réponses sont directement dérivées des données disponibles." +
                        "Par exemple, si un utilisateur demande : 'Quelles sont les opportunités de stages d'été ?', vous devriez répondre avec les opportunités spécifiques répertoriées dans le jeu de données. Une réponse exemplaire serait : 'Les opportunités de stages d'été comprennent X, Y et Z'." +
                        "Assurez-vous que toutes les réponses sont pertinentes par rapport aux données disponibles."
        ));
        this.messages.add(new Message("user", prompt));
    }
}