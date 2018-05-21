import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Alligator extends Application {

    private ObservableList<Article> data;
    private Scene adminScene;
    private ComboBox<String> levCB, levArtCB, taBortLevCB;
    private BorderPane bp;
    private Database db = new Database("database.db");
    private User currentUser = new User("Olof", true,true);

    public static void main(String args[]) {


        launch(args); //startar gui

    }
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Alligator Bioscience");
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(340);
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        GridPane.setMargin(gp,new Insets(10,10,10,10));
        TextField anv = new TextField();
        anv.setEditable(true);
        gp.add(new Text("  Användarnamn: "), 0,0);
        gp.add(anv,1,0);
        gp.add(new Text("  Lösenord: "),0,1);
        TextField pw = new TextField();
        pw.setEditable(true);
        gp.add(pw,1,1);

        Button logIn = new Button("Logga in");
        logIn.setOnAction(e->{
            if(true){//login(anv.getText(),pw.getText())){
                //lyckad login
                primaryStage.hide();
                loggedIn(new Stage());
            }else{
                //misslyckad login
                AlertBox.display("Meddelande","Inloggning misslyckad");
            }
        });
        Button createUser = new Button("Ny användare");
        createUser.setOnAction(e->{
            //TODO ny användare????
        });
        gp.add(createUser,0,2);
        gp.add(logIn,1,2);
        gp.setStyle("-fx-background-color: #FFFFFF;");
        Scene loginScene = new Scene(gp,400,240);
        loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.show();

    }
    private boolean login(String anv, String pw){
        User user = db.logIn(anv,pw);
        if(user.equals(null)){
            return false;
        }else{
            currentUser = user;
            return true;
        }

    }


    public void loggedIn(Stage primaryStage){

        primaryStage.setTitle("Alligator Bioscience");
        primaryStage.setMinHeight(320);
        primaryStage.setMinWidth(400);
        HBox upperButtons = new HBox(5), adminButtons = new HBox(5), bestButtons = new HBox(5);

        //sätter marginaler för den olika raderna av knappar
        BorderPane.setMargin(upperButtons, new Insets(10, 0, 10, 10));
        BorderPane.setMargin(adminButtons, new Insets(0, 0, 0, 10));
        BorderPane.setMargin(bestButtons, new Insets(0, 0, 0, 10));



        VBox artMeny = createArtMeny();
        VBox laggMeny = createLaggMeny();
        VBox taBortMeny = createTaBortMeny();


        // bygger de övre knapparna
        Button adminButton = new Button("Admin");
        adminButton.setOnAction(e -> {
            bp.setCenter(adminButtons);
            bp.setBottom(artMeny);
            levCB.setItems(db.getLevOptions());
        });
        Button bestButton = new Button("Beställningar");
        bestButton.setOnAction(e -> {
            bp.setCenter(bestButtons);
            bp.setBottom(laggMeny);
            levCB.setItems(db.getLevOptions());
        });
        Button rappButton = new Button("Rapporter");
        rappButton.setOnAction(e -> setRapp());

        Image image = new Image("logo.jpg");
        ImageView iv1 = new ImageView();
        iv1.setImage(image);


        //lägger de övre knapparna i horisontell låda //och bild?
        upperButtons.getChildren().addAll(adminButton, bestButton, rappButton,iv1);


        //skapar adminknappar
        Button artButton = new Button("Ny artikel");
        artButton.setOnAction(e -> {
            bp.setBottom(artMeny);
            levArtCB.setItems(db.getLevOptions());
        });

        Button taBortArtButton = new Button("Ta bort artikel");
        taBortArtButton.setOnAction(e -> {
            bp.setBottom(taBortMeny);
            taBortLevCB.setItems(db.getLevOptions());
        });



        //skapar beställningsknappar
        Button laggBest = new Button("Lägg ny beställning");
        laggBest.setOnAction(e -> {
            bp.setBottom(laggMeny);
            levCB.setItems(db.getLevOptions());
        });
        Button nyBestButton = new Button("För attest");
        nyBestButton.setOnAction(e -> showTable("Godkänn"));
        Button attestButton = new Button("Att beställa");
        attestButton.setOnAction(e -> showTable("Beställd"));
        Button godkButton = new Button("Beställda");
        godkButton.setOnAction(e -> showTable("Mottagen"));
        Button levButton = new Button("Levererade");
        levButton.setOnAction(e -> showTable("Ta bort"));

        //lägger adminknappar i horisontell låda
        adminButtons.getChildren().addAll(artButton,taBortArtButton);

        //lägger beställningsknappar i horisontell låda
        bestButtons.getChildren().addAll(laggBest, nyBestButton, attestButton, godkButton, levButton);


        bp = new BorderPane();
        bp.setTop(upperButtons);
        bp.setCenter(adminButtons);
        bp.setBottom(artMeny);
        bp.setStyle("-fx-background-color: #FFFFFF;");

        adminScene = new Scene(bp, 1080, 620);
        adminScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(adminScene);
        primaryStage.show();


    }

    private void setRapp(){
        VBox fullSearch = new VBox(5); //bp set center sökfunktion??
        HBox search = new HBox(10);
        TextField levVal = new TextField("");
        levVal.setEditable(true);
        levVal.setMaxWidth(75);
        TextField nameVal = new TextField("");
        nameVal.setEditable(true);
        nameVal.setMaxWidth(75);
        TextField nrVal = new TextField("");
        nrVal.setEditable(true);
        nrVal.setMaxWidth(75);
        DatePicker dp = new DatePicker();
        dp.setMaxWidth(100);
        DatePicker dtp = new DatePicker();
        dtp.setMaxWidth(100);
        Button srch = new Button("Filtrera"); //TODO implementera denna knappen, senare--, logga, text,login,snyggare(?), nåt mer?

        srch.setOnAction(e->{

        });
        search.getChildren().addAll(new Text("  Leverantör: " ), levVal, new Text("Namn: " ), nameVal, new Text("Nummer: "), nrVal, new Text("Från: "),dp, new Text("Till: "),dtp,srch);
        fullSearch.getChildren().addAll(new Text("  Filtrera: "),search);
        bp.setCenter(fullSearch);
        showTable("Rapporter");
    }

    private VBox createArtMeny(){
        VBox artMeny = new VBox(10);
        BorderPane.setMargin(artMeny, new Insets(10, 10, 400, 10));
        HBox levValArt = new HBox(5);
        ObservableList<String> levArtOptions = db.getLevOptions();
        levArtCB = new ComboBox<>(levArtOptions);
        levArtCB.setEditable(true);
        levValArt.getChildren().addAll(new Text("Leverantör: "), levArtCB);

        HBox prodValArt = new HBox(5);
        TextField prodValArtTx = new TextField("");
        prodValArtTx.setEditable(true);
        prodValArt.getChildren().addAll(new Text("Produktnamn: "), prodValArtTx);

        HBox prodNrValArt = new HBox(5);
        TextField prodNrValArtTx = new TextField("");
        prodNrValArtTx.setEditable(true);
        prodNrValArt.getChildren().addAll(new Text("Produktnummer: "), prodNrValArtTx);

        Button skapaArt = new Button("Lägg till artikel");
        skapaArt.setOnAction(e->{
            if(db.createArticle(levArtCB.getValue(), prodValArtTx.getText(),  prodNrValArtTx.getText())){
                AlertBox.display("Meddelande","Artikel tillagd");
            }
            else{
                AlertBox.display("Meddelande","Artikel kunde inte läggas till");
            }

            levArtCB.setItems(db.getLevOptions());
            levArtCB.setValue("");
            prodValArtTx.setText("");
            prodNrValArtTx.setText("");

        });

        artMeny.getChildren().addAll(levValArt,prodValArt,prodNrValArt, skapaArt);
        return artMeny;
    }
    private VBox createLaggMeny(){
        VBox laggMeny = new VBox(10);
        ComboBox<String> prodCB = new ComboBox<>(db.getProdOptions(""));

        BorderPane.setMargin(laggMeny, new Insets(10, 10, 400, 10));
        HBox levVal = new HBox(5);


        ObservableList<String> levOptions = db.getLevOptions();
        levCB = new ComboBox<>(levOptions);
        levCB.setOnAction(e-> prodCB.setItems(db.getProdOptions(levCB.getValue())));
        levCB.setEditable(false);
        levVal.getChildren().addAll(new Text("Leverantör: "), levCB);

        HBox prodVal = new HBox(5);

        prodCB.setEditable(false);
        prodVal.getChildren().addAll(new Text("Produkt: "), prodCB);
        HBox nrVal = new HBox(5);
        TextField nrTx = new TextField();
        nrTx.setEditable(false);
        nrVal.getChildren().addAll(new Text("Produktnummer: "), nrTx);
        prodCB.setOnAction(e-> nrTx.setText(db.getProdNr(levCB.getValue(),prodCB.getValue())));

        HBox prisVal = new HBox(5);
        TextField prisTx = new TextField();
        prisVal.getChildren().addAll(new Text("Cirkapris: "), prisTx);
        prisTx.setText("");

        HBox projVal = new HBox(5);
        ObservableList<String> projOptions =
                FXCollections.observableArrayList(
                        "3000"
                );
        ComboBox<String> projCB = new ComboBox<>(projOptions);
        projCB.setValue("3000");
        projCB.setEditable(true);

        projVal.getChildren().addAll(new Text("Projekt: "), projCB);

        HBox prioVal = new HBox(5);
        ObservableList<String> prioOptions =
                FXCollections.observableArrayList(
                        "Normal",
                        "Hög prioritet"
                );

        ComboBox<String> prioCB = new ComboBox<>(prioOptions);
        prioCB.setValue("Normal");
        prioCB.setEditable(false);
        prioVal.getChildren().addAll(new Text("Prioritet: "), prioCB);

        CheckBox chem = new CheckBox("Ny kemikalie");


        Button skapaBest = new Button("Lägg beställning");
        skapaBest.setOnAction(e->{
            confirmOrder(levCB.getValue(), prodCB.getValue(), nrTx.getText(),prisTx.getText(), projCB.getValue(), prioCB.getValue(), chem);
            levCB.setValue("");
            prodCB.setValue("");
            nrTx.setText("");
            prisTx.setText("");
            projCB.setValue("3000");
            prioCB.setValue("");
            chem.setSelected(false);
        } );



        laggMeny.getChildren().addAll(levVal, prodVal, nrVal, prisVal, projVal, prioVal, chem, skapaBest);
        return laggMeny;
    }
    private VBox createTaBortMeny(){
        VBox taBortMeny = new VBox(10);
        BorderPane.setMargin(taBortMeny, new Insets(10, 10, 400, 10));
        HBox levValArt = new HBox(5);
        ObservableList<String> levArtOptions = db.getLevOptions();
        taBortLevCB = new ComboBox<>(levArtOptions);
        taBortLevCB.setEditable(false);
        levValArt.getChildren().addAll(new Text("Leverantör: "), taBortLevCB);

        HBox prodVal = new HBox(5);
        ComboBox<String> prodCB = new ComboBox<>(db.getProdOptions(""));
        prodCB.setEditable(false);
        taBortLevCB.setOnAction(e-> prodCB.setItems(db.getProdOptions(taBortLevCB.getValue())));
        prodVal.getChildren().addAll(new Text("Produkt: "), prodCB);
        HBox nrVal = new HBox(5);
        TextField nrTx = new TextField();
        nrTx.setEditable(false);
        nrVal.getChildren().addAll(new Text("Produktnummer: "), nrTx);
        prodCB.setOnAction(e-> nrTx.setText(db.getProdNr(taBortLevCB.getValue(),prodCB.getValue())));

        Button taBortArt = new Button("Ta bort artikel");
        taBortArt.setOnAction(e->{
            if(db.removeArticle(taBortLevCB.getValue(), prodCB.getValue(), nrTx.getText() )){
                AlertBox.display("Meddelande","Artikel borttagen");
                taBortLevCB.setItems(db.getLevOptions());
                taBortLevCB.setValue("");
                prodCB.setValue("");
                nrTx.setText("");
            }
            else{
                AlertBox.display("Meddelande","Artikel kunde inte tas bort");
            }

        });

        taBortMeny.getChildren().addAll(levValArt,prodVal,nrVal,taBortArt);
        return taBortMeny;
    }
    private void confirmOrder(String lev, String prod, String nr, String pris, String proj, String prio, CheckBox chem){
        String chemText = " ";
        if(chem.isSelected()){
            chemText = "Ny kemikalie";
        }
        db.addBest(lev,prod,nr,pris,proj,prio,chemText,currentUser.getName()); //läggbest
    }


    /*
    Hämtar data ifrån databasen. Skapar sedan kolumner för tabellen och gör en tabell.
    Utnyttjar klassen buttoncell för knapparna i tabellen.
     */
    private void showTable(String name){
        TableView<Article> tab = new TableView<>();
        tab.setMaxHeight(385);
        data = db.getTable(name); //TODO onödigt att skapa om alla kolumner vid varje kall?

        TableColumn<Article,String> levCol = new TableColumn<>("Leverantör");
        levCol.setMinWidth(100);
        levCol.setCellValueFactory(new PropertyValueFactory<>("lev"));
        TableColumn<Article,String> projCol = new TableColumn<>("Projekt");
        projCol.setMinWidth(100);
        projCol.setCellValueFactory(new PropertyValueFactory<>("proj"));
        TableColumn<Article,String> nameCol = new TableColumn<>("Namn");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Article,String> nrCol = new TableColumn<>("Nummer");
        nrCol.setMinWidth(100);
        nrCol.setCellValueFactory(new PropertyValueFactory<>("nr"));
        TableColumn<Article,String> prioCol = new TableColumn<>("Prioritet");
        prioCol.setMinWidth(100);
        prioCol.setCellValueFactory(new PropertyValueFactory<>("prio"));
        TableColumn<Article,String> prisCol = new TableColumn<>("Pris");
        prisCol.setMinWidth(100);
        prisCol.setCellValueFactory(new PropertyValueFactory<>("pris"));
        TableColumn<Article,String> chemCol = new TableColumn<>("Ny kemikalie");
        chemCol.setMinWidth(100);
        chemCol.setCellValueFactory(new PropertyValueFactory<>("chemText"));
        TableColumn<Article,String> dateCol = new TableColumn<>("Skapad");
        dateCol.setMinWidth(100);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Article,String> bestCol = new TableColumn<>("Beställd");
        bestCol.setMinWidth(100);
        bestCol.setCellValueFactory(new PropertyValueFactory<>("ordered"));
        TableColumn<Article,String> recCol = new TableColumn<>("Mottagen");
        recCol.setMinWidth(100);
        recCol.setCellValueFactory(new PropertyValueFactory<>("received"));
        TableColumn<Article,String> userCol = new TableColumn<>("Användare");
        userCol.setMinWidth(100);
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));


        TableColumn col_kyl = new TableColumn<>("Placeras i");
        col_kyl.setMinWidth(100);
        col_kyl.setSortable(false);
        col_kyl.setCellFactory(e->new ListCell());

        TableColumn col_kylRes = new TableColumn<>("Placerad i");
        col_kylRes.setMinWidth(100);
        col_kylRes.setCellValueFactory(new PropertyValueFactory<>("kyl"));


        TableColumn col_action = new TableColumn<>("Action");
        col_action.setMinWidth(100);
        col_action.setSortable(false);
        col_action.setCellFactory(e-> new ButtonCell(name));


        tab.setItems(data);

        //case system istället.

        switch(name){
            case "Godkänn":
                tab.getColumns().addAll(levCol, nameCol, prisCol, projCol, chemCol, dateCol, col_action);
                break;
            case "Beställd":
                tab.getColumns().addAll(levCol, nameCol, nrCol, prioCol,userCol, dateCol, col_action);
                break;
            case "Mottagen":
                tab.getColumns().addAll(levCol, nameCol, nrCol, userCol, bestCol, col_kyl, col_action);
                break;
            case "Ta bort": /// ta bort??? ska inte ha någon add
                tab.getColumns().addAll(levCol, nameCol, nrCol, prioCol,userCol, dateCol, col_kylRes);
                break;
            case "Rapporter":
                tab.getColumns().addAll(levCol, nameCol, nrCol,prisCol,projCol, prioCol,userCol, dateCol, recCol, col_kylRes);
                break;
            default:
                break;

        }
        bp.setBottom(tab);

    }
    private class ListCell extends TableCell<Article, Boolean>{
        private ComboBox<String> alts = new ComboBox<>();
        ListCell(){
            ObservableList<String> options = FXCollections.observableArrayList();
            options.add("RT");
            options.add("Kyl");
            options.add("Frys -20");
            options.add("Frys -80");
            alts.setOnAction(event -> {
                Article temp = data.get(getTableRow().getIndex());
                db.setKyl(temp.getID(),alts.getValue());
            });
            alts.setItems(options);
            alts.setEditable(false);
            alts.setValue("RT");

        }
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(alts);
            }
        }
    }
    private class ButtonCell extends TableCell<Article, Boolean> {
        final Button cellButton = new Button();
        ButtonCell(String name){

            cellButton.setText(name);
            cellButton.setOnAction(e->{
                int i = getTableRow().getIndex();
                ObservableList<Article> data = getTableView().getItems();
                Article selArt = data.get(i);
                db.orderAccepted(selArt.getTable(), selArt.getID()); //kanske också kyl
                showTable(name);
            });

        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }
}

