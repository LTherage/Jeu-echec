import java.io.*;
import java.util.*;
import navigator.*;

public class Echecs {

    Navigator nav = new Navigator();


    static boolean[][] casesAttaquees = new boolean[8][8]; // pour les conditions d'echec & echec et mat
    static int debutPartie = 0; // pour le double coups des pions
    static char[] position = {'a','b','c','d','e','f','g','h'}; // position X echiquier
    static int departX; // PositionX de piece choisi
    static int departY; // PositionY de piece choisi
    static int arriveX; // PositionX de l'arrivé voulu
    static int arriveY; // PositionX de l'arrivé voulu
    static int indice_lettre; // conversion lettre en chiffre pour parcours du plateau
    boolean mat = true; // condition d'arret ( pas faite )
    static long tempsGlobal = 15*60*1000;
    static long temps_Blanc = tempsGlobal;
    static long temps_noir = tempsGlobal;
    static long debutTour;
    static boolean est_blanc = true;

    
    String[][] ResetPlateau() {
        String[][] plateau = {
            {"tourN", "cavalierN", "fouN", "reineN", "roiN", "fouN", "cavalierN", "tourN"},
            {"pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN", "pionN"},
            {"vide", "vide", "vide", "vide", "vide", "vide", "vide", "vide"},
            {"vide", "vide", "vide", "vide", "vide", "vide", "vide", "vide"},
            {"vide", "vide", "vide", "vide", "vide", "vide", "vide", "vide"},
            {"vide", "vide", "vide", "vide", "vide", "vide", "vide", "vide"},
            {"pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB", "pionB"},
            {"tourB", "cavalierB", "fouB", "roiB", "reineB", "fouB", "cavalierB", "tourB"}
        };
        return plateau;
    }

    void Afficher(String[][] plateau) { // Samuel
        // Données : un tableau à deux dimensions se nommant "plateau"
        // Résultat : L'affichage du damier de l'echec avec lignes et colonnes
        int décalage = 0;
        for (int i = 0; i < 8; i++) {
            nav.println("       <tr class='colonne'>");
            for (int j = 0; j < 8; j++) {
                String piece = plateau[i][j];
                String imagePath = chemin(piece);
                int indice = i%2;
                int paire = j+indice;
                nav.println("           <img src='" + imagePath + "' class=paire"+paire%2+">"); 
            }
            nav.println("           <br>");
            nav.println("   </tr");
        }
        nav.println("       <br>");
    }

    // Retourne le chemin de l'image correspondant à la pièce
    String chemin(String piece) { // Samuel 
        // Données : Un paramètre formel se nommant piece qui est un String
        // Résultat : // Retourne le chemin de l'image correspondant à la pièce
        switch (piece) {
            case "tourN":
                return "tourN.svg";
            case "cavalierN":
                return "cavalierN.svg";
            case "fouN":
                return "fouN.svg";
            case "reineN":
                return "reineN.svg";
            case "roiN":
                return "roiN.svg";
            case "pionN":
                return "pionN.svg";
            case "tourB":
                return "tourB.svg";
            case "cavalierB":
                return "cavalierB.svg";
            case "fouB":
                return "fouB.svg";
            case "reineB":
                return "reineB.svg";
            case "roiB":
                return "roiB.svg";
            case "pionB":
                return "pionB.svg";
            default:
                return "vide.svg"; // Image pour les cases vides
        }
    }



   
    public static boolean positionsValides(int departX, int departY, int arriveX, int arriveY) { // Alexis
         // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
         // arriveX pour vérifier que la colonne ne dépasse pas le damier ainsi que pour arriveY 
         // Résultat :  Verifie les positions de départ et d'arrivées pour déplacer les pions
        return departX >= 0 && departX < 8 && departY >= 0 && departY < 8 &&
               arriveX >= 0 && arriveX < 8 && arriveY >= 0 && arriveY < 8;
    }




    public static boolean verifierDeplacementPion(int departX, int departY, int arriveX, int arriveY, String[][] plateau, boolean premierCoup) { // Alexis ( marche que poure e2 e4 & f2 f4 à saisir 2x d'affiler)
        // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
        // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
        // Un tableau à deux dimensions représentant notre plateau de jeu
        // Un booleen premierCoup 
        // Résultat : // Verifie si on peut déplacer le pion des coordonnées de départs aux coordonnées d'arrivées

        String pion = plateau[departX][departY];
        boolean estBlanc = pion.equals("pionB");
        int direction = estBlanc ? 1 : -1;
    
        // Déplacement vertical (1 ou 2 cases)
        if (departX == arriveX) {
            int pas = arriveY - departY;
            if (pas == direction && plateau[arriveX][arriveY].equals("vide")) {
                return true; // Avance d'une case
            }
            if (premierCoup && pas == 2 * direction && 
                plateau[arriveX][arriveY].equals("vide") && 
                plateau[departX][departY + direction].equals("vide")) {
                return true; // Avance de deux cases au premier coup
            }
        }
    
        // Capture en diagonale
        if (Math.abs(departX - arriveX) == 1 && arriveY - departY == direction) {
            if (!plateau[arriveX][arriveY].equals("vide") && 
                !plateau[arriveX][arriveY].equals(pion)) {
                return true; // Capture valide
            }
        }
        return false; // Si aucune condition n'est remplie
    }
    



    // FOU
    public boolean verifierDeplacementFou(int departX, int departY, int arriveX, int arriveY, String[][]plateau) { // Alexis ( ne fonctionne pas )
        // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
        // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
        // Un tableau à deux dimensions représentant notre plateau de jeu
        // Un booleen premierCoup 
        // Résultat : // Verifie si on peut déplacer le fou des coordonnées de départs aux coordonnées d'arrivées 
        if (!positionsValides(departX, departY, arriveX, arriveY)) {
            return false;
        }
        String fou = plateau[departY][departX];
        if (fou.equals("vide")) {
            return false; // Aucune pièce à déplacer
        }
        int diffX = Math.abs(arriveX - departX);
        int diffY = Math.abs(arriveY - departY);
        if (diffX != diffY) {
            return false; // Pas un déplacement diagonal
        }

        // Vérification des cases sur le chemin
        int pasX = (arriveX - departX) / diffX;
        int pasY = (arriveY - departY) / diffY;
        int x = departX + pasX;
        int y = departY + pasY;

        while (x != arriveX && y != arriveY) {
            if (!plateau[y][x].equals("vide")) {
                return false; // Chemin bloqué
            }
            x += pasX;
            y += pasY;
        }
        return true;
    }

    //TOUR
    public static boolean verifierDeplacementTour(int departX, int departY, int arriveX, int arriveY, String[][] plateau) { //Alexis ( ne fonctionne pas )
            // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
            // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
            // Un tableau à deux dimensions représentant notre plateau de jeu
            // Un booleen premierCoup 
            // Résultat : // Verifie si on peut déplacer la tour des coordonnées de départs aux coordonnées d'arrivées
            if (departX == arriveX || departY == arriveY) {
                int xDir = 0;
                int yDir = 0;
    
                // Déterminer la direction horizontale (x)
                if (departX != arriveX) {
                    if (arriveX > departX) {
                        xDir = 1;  // Mouvement vers la droite
                    } else {
                        xDir = -1; // Mouvement vers la gauche
                    }
                }
    
                // Déterminer la direction verticale (y)
                if (departY != arriveY) {
                    if (arriveY > departY) {
                        yDir = 1;  // Mouvement vers le bas
                    } else {
                        yDir = -1; // Mouvement vers le haut
                    }
                }
    
                // Vérification des obstacles
                int x = departX + xDir;
                int y = departY + yDir;
    
                while (x != arriveX || y != arriveY) {

                    if (!plateau[x][y].equals("vide")) { // Si obstacle = case occupée
                        return false;
                    }
                    x += xDir; 
                    y += yDir; 
                }
                return true; 
            }
            return false; 
        }


    //REINE
    public boolean verifierDeplacementReine(int departX, int departY, int arriveX, int arriveY, String[][] plateau) { // Alexis ( ne fonctionne pas )
            // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
            // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
            // Un tableau à deux dimensions représentant notre plateau de jeu
            // Un booleen premierCoup 
            // Résultat : // Verifie si on peut déplacer la reine des coordonnées de départs aux coordonnées d'arrivées
            // Vérification des positions valides
            // Vérification des positions valides
            if (!positionsValides(departX, departY, arriveX, arriveY)) {
                return false;
            }
    
            // Vérifie si la Reine se déplace horizontalement ou verticalement
            if (departX == arriveX || departY == arriveY) {
                int xDir = 0;
                int yDir = 0;
    
                // Déterminer la direction horizontale (x)
            if (arriveX > departX) {
                xDir = 1;  // Mouvement vers la droite
            } else if (arriveX < departX) {
                xDir = -1; // Mouvement vers la gauche
            }

            // Déterminer la direction verticale (y)
            if (arriveY > departY) {
                yDir = 1;  // Mouvement vers le bas
            } else if (arriveY < departY) {
                yDir = -1; // Mouvement vers le haut
            }
    
                // Vérification des obstacles sur le chemin
                int x = departX + xDir;
                int y = departY + yDir;
    
                while (x != arriveX || y != arriveY) {
                    if (plateau[y][x].equals("vide")) {
                        return false; // Chemin bloqué
                    }
                    x += xDir;
                    y += yDir;
                }
                return true; // Déplacement valide
            }
    
            // Vérifie si la Reine se déplace en diagonale
            int diffX = Math.abs(arriveX - departX);
            int diffY = Math.abs(arriveY - departY);
            if (diffX == diffY) {
                int pasX = (arriveX - departX) / diffX;
                int pasY = (arriveY - departY) / diffY;
                int x = departX + pasX;
                int y = departY + pasY;
    
                while (x != arriveX && y != arriveY) {
                    if (plateau[y][x].equals("vide")) {
                        return false; // Chemin bloqué
                    }
                    x += pasX;
                    y += pasY;
                }
                return true; // Déplacement valide
            }
    
            // Si le mouvement n'est ni horizontal, vertical, ni diagonal
            return false;
        }


    //ROI
    public static boolean verifierDeplacementRoi(int departX, int departY, int arriveX, int arriveY, String[][] plateau, boolean[][] casesAttaquees) { // Alexis ( ne fonctionne pas )
        // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
        // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
        // Un tableau à deux dimensions représentant notre plateau de jeu
        // Un tableau à deux dimensions représnetant des booleens 
        // Résultat : // Verifie si on peut déplacer le roi des coordonnées de départs aux coordonnées d'arrivées
        if (!positionsValides(departX, departY, arriveX, arriveY)) {
            return false;
        }
    
        if (Math.abs(arriveX - departX) > 1 || Math.abs(arriveY - departY) > 1) {
            return false;
        }

        if (!plateau[arriveY][arriveX].equals("vide") && plateau[departY][departX].equals(plateau[arriveY][arriveX])) {
        }

        if (casesAttaquees[arriveY][arriveX]) {
            return false;
        }
    
        plateau[arriveY][arriveX] = plateau[departY][departX];
        plateau[departY][departX] = "vide";
    
        return true;
    }
    
    //CAVALIER
    //Pas fait

        String[][] Deplacement(int departX, int departY, int arriveX, int arriveY, String[][] plateau) { // Samuel ( dépendant des condition de déplacement )
            // Données : 4 paramètres formels de type entier : departX pour la colonne du pion actuel et departY pour la ligne
            // arriveX pour déplacer le pion de la colonne departX à arriveX et déplacer le pion de la ligne departY à arriveY
            // Un tableau à deux dimensions représentant notre plateau de jeu 
            // Résultat : // Verifie si on peut déplacer le pion des coordonnées de départs aux coordonnées d'arrivées
            if (!positionsValides(departX, departY, arriveX, arriveY)) {
                nav.println("Déplacement invalide : positions hors limites.");
                return plateau;
            }
            String piece = plateau[departY][departX];
            boolean deplacementValide = false;
            switch (piece) {
                case "tourN":
                case "tourB":
                    deplacementValide = verifierDeplacementTour(departX, departY, arriveX, arriveY, plateau);
                    break;
                case "fouN":
                case "fouB":
                    deplacementValide = verifierDeplacementFou(departX, departY, arriveX, arriveY, plateau);
                    break;
                case "reineN":
                case "reineB":
                    deplacementValide = verifierDeplacementReine(departX, departY, arriveX, arriveY, plateau);
                    break;
                case "roiN":
                case "roiB":
                    deplacementValide = verifierDeplacementRoi(departX, departY, arriveX, arriveY, plateau, casesAttaquees);
                    break;
                case "pionN":
                case "pionB":
                    deplacementValide = verifierDeplacementPion(departX, departY, arriveX, arriveY, plateau, debutPartie==0);
                    break;
            }
            if (deplacementValide) {
                plateau[arriveY][arriveX] = piece;
                plateau[departY][departX] = "vide";
                nav.println("<p> Déplacement réussi ! </p>");
                }   
            else {
                nav.println("<p> Déplacement invalide. </p>");
            }
            
            return plateau;
        }

    void Requete(String[][] plateau) { // Ludovic 
        // Données : Un tableau à deux dimensions répresentant notre plateau 
        // Resultat : Formuaire de départ arrivée pour déplacer les pions
        nav.println("""
            <form method="POST">
                <label for="depart">Position de départ :</label>
                <input type="text" id="depart" name="depart" required><br>
            
                <label for="arrivee">Position d'arrivée :</label>
                <input type="text" id="arrivee" name="arrivee" required><br>
                <button class="bouton" type="submit">Valider</button>
            </form>
            
        """);
    
        if (nav.containsKey("depart") && nav.containsKey("arrivee")) {
            String depart = nav.get("depart");
            String arrivee = nav.get("arrivee");
            TraitementSaisi(depart,arrivee, plateau);
        }
    }

    // Traduis le coup joué
    void TraitementSaisi(String depart, String arrivee, String[][] plateau) { // Samuel
        // Données : 2 String représentants les coordonnées et notre plateau
        // Résultat : Traitement du déplacement 
        // Conversion des coordonnées de départ
        // Conversion des coordonnées de départ
        int departX = depart.charAt(0) - 'a';
        int departY = 8 - (depart.charAt(1) - '0');
    
        int arriveX = arrivee.charAt(0) - 'a';
        int arriveY = 8 - (arrivee.charAt(1) - '0');


        nav.println("Indices calculés - departX: " + departX + ", departY: " + departY);
        nav.println("Indices calculés - arriveX: " + arriveX + ", arriveY: " + arriveY);


        Deplacement(departX, departY, arriveX, arriveY, plateau);
    }


    boolean echec(String color, String[][] plateau, boolean[][] casesAttaquees){ // Ludovic
        // Données : string couleur (joueur), plateau, case possible du roi
        // Résultat : Evalue s'il y a echec ( pas fonctionnel )
        for(int i=0; i<8; i++){
            for(int j=0; j<8; i++){
                if (plateau[i][j].equals("roi" + color) ){
                    return casesAttaquees[i][j];
                }
            }
        }
         return false;
    }

    boolean estEchecetMat(String color , String[][] plateau, boolean[][] casesAttaquees){// Ludovic
        // Données : string couleur (joueur), plateau, case possible du roi
        // Résultat : Evalue s'il y a echec & mat ( pas fonctionnel )
        int roi_ligne = -1000, roi_colonne = -1000; // Variable pour trouver le roi 
        for(int i=0; i<8; i++){
            for(int j=0; j<8; i++){
                if(plateau[i][j].equals("roi" + color)){
                    // Position réel trouvé donc roi_ligne et roi_colonne = i et j
                    roi_ligne = j;
                    roi_colonne = i;
                    break;
                }
            }
        }
        if(roi_ligne == -1000 && roi_colonne == -1000) return false;

        for(int dist_X= -1 ; dist_X <= 1; dist_X++){
            for(int dist_Y = -1 ; dist_Y <= 1 ; dist_Y++ ){
                if(dist_X == 0 && dist_Y == 0) continue;
    
                int ligne = roi_ligne + dist_X;
                int colonne = roi_colonne + dist_Y;
    
                if(positionsValides(ligne, colonne, ligne, colonne) && !casesAttaquees[ligne][colonne] && (plateau[ligne][colonne].equals("vide")  && plateau[ligne][colonne].startsWith(color))){
                    return false;
                }
            }
        }
        return true;
    }

    void FindePartie(){ // Samuel
        // Données : X
        // Résultat : Affichage de l'écran de fin de partie ( lorsqu'on clique sur le bouton arreter )
        nav.println("<!DOCTYPE html>");
        nav.println("<html lang='fr'>");
        nav.println("   <head>");
        nav.println("       <meta charset='UTF-8'/>");
        nav.println("       <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        nav.println("       <title>Echec et Mat - Jeu d'Echecs</title>");
        nav.println("       <style>");
        nav.println("           body {");
        nav.println("               font-family: 'Arial', sans-serif;");
        nav.println("               background-color: #1e1e1e;");
        nav.println("               color: white;");
        nav.println("               margin: 0;");
        nav.println("               padding: 0;");
        nav.println("               display: flex;");
        nav.println("               justify-content: center;");
        nav.println("               align-items: center;");
        nav.println("               height: 100vh;");
        nav.println("               text-align: center;");
        nav.println("               flex-direction: column;");
        nav.println("               background-image: url('https://cdn.pixabay.com/photo/2017/02/21/21/43/board-2081683_960_720.jpg');");
        nav.println("               background-size: cover;");
        nav.println("               background-position: center;");
        nav.println("               box-sizing: border-box;");
        nav.println("           }");
        nav.println("           h1 {");
        nav.println("               font-size: 48px;");
        nav.println("               color: #FFCC00;");
        nav.println("               text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.5);");
        nav.println("               margin-bottom: 20px;");
        nav.println("           }");
        nav.println("           .message {");
        nav.println("               font-size: 36px;");
        nav.println("               color: #FF5722;");
        nav.println("               text-transform: uppercase;");
        nav.println("               font-weight: bold;");
        nav.println("               margin-bottom: 30px;");
        nav.println("           }");
        nav.println("           .victory-image {");
        nav.println("               max-width: 200px;");
        nav.println("               margin: 20px;");
        nav.println("               border-radius: 50%;");
        nav.println("               border: 5px solid #FF5722;");
        nav.println("               box-shadow: 0 0 10px rgba(0, 0, 0, 0.7);");
        nav.println("           }");
        nav.println("           .buttons {");
        nav.println("               display: flex;");
        nav.println("               gap: 20px;");
        nav.println("               margin-top: 30px;");
        nav.println("           }");
        nav.println("           .button {");
        nav.println("               padding: 15px 30px;");
        nav.println("               background-color: #FF5722;");
        nav.println("               color: white;");
        nav.println("               font-size: 20px;");
        nav.println("               font-weight: bold;");
        nav.println("               border: none;");
        nav.println("               border-radius: 8px;");
        nav.println("               cursor: pointer;");
        nav.println("               box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);");
        nav.println("               transition: all 0.3s ease;");
        nav.println("           }");
        nav.println("           .button:hover {");
        nav.println("               background-color: #e64a19;");
        nav.println("               box-shadow: 0 6px 10px rgba(0, 0, 0, 0.3);");
        nav.println("               transform: translateY(-3px);");
        nav.println("           }");
        nav.println("           .button:active {");
        nav.println("               background-color: #d84315;");
        nav.println("               box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);");
        nav.println("               transform: translateY(2px);");
        nav.println("           }");
        nav.println("       </style>");
        nav.println("   </head>");
        nav.println("   <body>");
        nav.println("       <h1>Fin de Partie</h1>");
        nav.println("       <div class='message'>Echec et Mat !</div>");
        nav.println("       <div class='message'>Le joueur Blanc a gagné !</div>");
        nav.println("       <img class='victory-image' src='blanc.jpg' alt='Victoire des Blancs'/>");
        nav.println("   </body>");
        nav.println("</html>");
        // manque de l'arret de la boucle navigateur
        }



        void commencerTour(){ //Ludovic
            // Données : X
            // Résultat : Initialise le chronometre ( pas fonctionnel )
            debutTour = System.currentTimeMillis();
        }
    
        void terminerTour(){ // Ludovic 
            // Données : X
            // Résultat : arreter le chronometre ( pas fonctionnel )
            long temps_ecoule = System.currentTimeMillis() - debutTour;
    
            if(est_blanc){
                temps_Blanc -=temps_ecoule;
            }
            else {
                temps_noir -= temps_ecoule;
            }
    
            if (temps_Blanc <= 0){
                nav.println("<h2> Temps écoulé pour les blancs ! Les noirs gagnent.</h2>");
                System.exit(0);
            }
            if (temps_noir <=0){
                nav.println("<h2> Temps écoulé pour les Noirs ! Les blancs gagnent.</h2>");
                System.exit(0);
            }
        }
    
        String formatage_temps(long temps){ // Ludovic
            // Données : 1 Long du temps
            // Résultat : Initialise le chronometre
            long secondes = temps / 1000;
            long minutes = secondes / 60;
            secondes %= 60;
            return String.format("%02d:%02d", minutes, secondes);
        }

        void run() { // Samuel
            String[][] plateau = ResetPlateau(); // création du tableau
            while(mat){
                nav.beginPage();
                nav.allowDownload("findepartie.html"); //importation de l'écran de fin de partie
                nav.allowDownload("tourN.svg"); // importation des images des pieces du jeux
                nav.allowDownload("vide.svg");
                nav.allowDownload("pionB.svg");
                nav.allowDownload("reineB.svg");
                nav.allowDownload("roiB.svg");
                nav.allowDownload("fouB.svg");
                nav.allowDownload("cavalierB.svg");
                nav.allowDownload("tourB.svg");
                nav.allowDownload("pionN.svg");
                nav.allowDownload("roiN.svg");
                nav.allowDownload("cavalierN.svg");
                nav.allowDownload("fouN.svg");
                nav.allowDownload("reineN.svg");
                nav.allowDownload("blanc.jpg");
                nav.allowDownload("noir.avif");
                nav.println("""
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta charset="UTF-8"/>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Jeux d'Echecs</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #a08888;
                                padding: 20px;
                                margin: 0;
                                text-align: center;
                            }
                            .plateau {
                                display: grid;
                                grid-template-columns: repeat(8, 1fr);
                                grid-gap: 5px;
                                width: 300px;
                                margin: 0 auto;
                            }
                            .case {
                                width: 30px;
                                height: 30px;
                                border: 1px solid #333;
                            }
                            .ligne {
                                display: flex;
                            }
                            .paire0 {
                                width: 100%;
                                height: 100%;
                                max-width: 100px;
                                background-image: url('blanc.jpg');
                                border-radius: 3px;
                                margin-right: 1px;
                                margin-top: 2px;
                            }
                            .paire1 {
                                width: 100%;
                                height: 100%;
                                max-width: 100px;
                                background-image: url('noir.avif');
                                border-radius: 3px;
                                margin-right: 1px;
                                margin-top: 2px;
                            }
                            header {
                                font-family: Georgia, 'Times New Roman', Times, serif;
                                text-transform: uppercase;
                                color: #f2ebe7;
                                font-size: 48px;
                                letter-spacing: 3px;
                                text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.5);
                                margin-bottom: 20px;
                                padding: 10px 20px;
                                background-color: rgba(0, 0, 0, 0.6);
                                border-radius: 10px;
                                display: inline-block;
                            }
                            form {
                                max-width: 400px;
                                margin: 40px auto;
                                padding: 20px;
                                border: 1px solid #ccc;
                                border-radius: 10px;
                                background-color: #f9f9f9;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                            }
                            label {
                                display: block;
                                font-size: 16px;
                                margin-bottom: 8px;
                                font-weight: bold;
                                color: #fff;
                            }
                            input[type='text'] {
                                max-width: 300px;
                                padding: 10px;
                                margin-bottom: 15px;
                                border: 1px solid #ddd;
                                border-radius: 5px;
                                font-size: 16px;
                                box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
                                transition: border-color 0.3s ease, box-shadow 0.3s ease;
                            }
                            input[type='text']:focus {
                                border-color: #ff5722;
                                box-shadow: 0 0 8px rgba(255, 87, 34, 0.2);
                                outline: none;
                            }
                            .bouton {
                                background-color: #ff5722;
                                color: white;
                                font-size: 18px;
                                font-weight: bold;
                                margin-bottom: 2rem;
                                padding: 12px 20px;
                                border: none;
                                border-radius: 8px;
                                cursor: pointer;
                                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
                                transition: all 0.3s ease;
                                display: block;
                                margin: 0 auto;
                                text-align: center;
                            }
                            .bouton:hover {
                                background-color: #e64a19;
                                box-shadow: 0 6px 10px rgba(0, 0, 0, 0.3);
                                transform: translateY(-2.5px);
                            }
                            .bouton:active {
                                background-color: #d84315;
                                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
                                transform: translateY(1.5px);
                            }
                        </style>
                    </head>
                    <body>
                """);

                if(debutPartie==0){
                    nav.println("       <header> Jeux D'echecs 2 Joueurs !</header><br>");
                }
                nav.println("""
                    <a href="findepartie.html">
                        <button class="bouton">Arreter</button>
                    </a>
                """);
                nav.println("<br>");
                nav.println("<h2>Temps restant</h2>");
                nav.println("<p>Blanc : " + formatage_temps(temps_Blanc) + "</p>");
                nav.println("<p>Noir : " + formatage_temps(temps_noir) + "</p>");

                // Commencer le tour
                nav.println("<h1>Au tour des " + (est_blanc ? "blancs" : "noirs") + " !</h1>");
                commencerTour(); // Démarrer le chronomètre
                Afficher(plateau);
                Requete(plateau);
                // Passer au tour suivant
                est_blanc = !est_blanc;
                nav.println("</body>");
                nav.println("</html>");
                nav.endPage();
            }
            FindePartie();
        }
    public static void main(String[] args) {
        new Echecs().run();
    }
}
