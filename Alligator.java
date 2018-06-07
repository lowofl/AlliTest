import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Alligator extends Application {

    private ObservableList<Article> data;
    private BorderPane bp;
    private ChoiceBox levCB, levArtCB;
    private Database db = new Database("database.db");
    private User currentUser;
    private HBox upperButtons, adminButtons, bestButtons, anvButtons;
    private VBox fullSearch;
    public static void main(String args[]) {


        launch(args); //startar gui

    }
    @Override
    public void start(Stage primaryStage){

        //lägg över från lev till rapp
        db.clearLev();
        primaryStage.setTitle("Alligator Bioscience");
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(340);
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        GridPane.setMargin(gp,new Insets(10,10,10,1220));
        TextField anv = new TextField();
        anv.setEditable(true);
        gp.add(new Text("  Användarnamn: "), 0,0);
        gp.add(anv,1,0);
        gp.add(new Text("  Lösenord: "),0,1);
        PasswordField pw = new PasswordField();
        pw.setEditable(true);
        gp.add(pw,1,1);

        Button logIn = new Button("Logga in");
        logIn.setOnAction(e->{
            if(login(anv.getText(),pw.getText())){
                //lyckad login
                primaryStage.hide();
                loggedIn(new Stage());
            }else{
                //misslyckad login
                AlertBox.display("Meddelande","Inloggning misslyckad");
            }
        });
        gp.add(logIn,1,2);
        gp.setStyle("-fx-background-color: #FFFFFF;");
        Scene loginScene = new Scene(gp,400,240);
        loginScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        loginScene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                logIn.fire();
                event.consume();
            }
        });
        primaryStage.setScene(loginScene);
        primaryStage.show();

    }
    private boolean login(String anv, String pw){
        User user = db.logIn(anv,pw);
        if(user.getName().equals("")){
            return false;
        }else{
            currentUser = user;
            return true;
        }

    }
    private void loggedIn(Stage primaryStage){
        upperButtons = new HBox(5);
        adminButtons = new HBox(5);
        bestButtons = new HBox(5);
        anvButtons = new HBox(5);
        primaryStage.setTitle("Alligator Bioscience");
        primaryStage.setMinHeight(620);
        primaryStage.setMinWidth(1200);


        //sätter marginaler för den olika raderna av knappar
        BorderPane.setMargin(upperButtons, new Insets(10, 0, 10, 10));



        GridPane artMeny = createArtMeny();
        GridPane laggMeny = createLaggMeny();
        GridPane taBortMeny = createTaBortMeny();
        GridPane taBortAnvMeny = createTaBortAnvMeny();
        GridPane addAnvMeny = createAddAnvMeny();


        // bygger de övre knapparna
        Button adminButton = new Button("Artiklar");
        adminButton.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(adminButtons,artMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });
        Button bestButton = new Button("Beställningar");
        bestButton.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(bestButtons,laggMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });
        Button rappButton = new Button("Rapporter");
        rappButton.setOnAction(e -> setRapp());

        Button persButton = new Button("Personal");
        persButton.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(anvButtons,addAnvMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });

        Button logOut = new Button("Logga ut");
        logOut.setOnAction(e -> start(primaryStage));


        Image image = new Image("logo.png");
        ImageView iv1 = new ImageView();
        iv1.setImage(image);


        //lägger de övre knapparna i horisontell låda //och bild?
        upperButtons.getChildren().addAll(adminButton, bestButton, rappButton, persButton,logOut, iv1);


        //skapar adminknappar
        Button artButton = new Button("Ny artikel");
        artButton.setOnAction(e ->{
            VBox temp = new VBox(10);
            temp.getChildren().addAll(adminButtons,artMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });

        Button taBortArtButton = new Button("Ta bort artikel");
        taBortArtButton.setOnAction(e ->{
            VBox temp = new VBox(10);
            temp.getChildren().addAll(adminButtons,taBortMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });


        //skapar knappar för användarhantering

        Button taBortAnvButton = new Button("Ta bort användare");
        taBortAnvButton.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(anvButtons,taBortAnvMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });

        Button addAnvButton = new Button("Lägg till användare");
        addAnvButton.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(anvButtons,addAnvMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });

        anvButtons.getChildren().addAll(addAnvButton,taBortAnvButton);

        //skapar beställningsknappar
        Button laggBest = new Button("Lägg ny beställning");
        laggBest.setOnAction(e -> {
            VBox temp = new VBox(10);
            temp.getChildren().addAll(bestButtons,laggMeny);
            BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
            bp.setCenter(temp);
        });
        Button nyBestButton = new Button("För attest");
        nyBestButton.setOnAction(e -> showTable(0,null));
        Button attestButton = new Button("Att beställa");
        attestButton.setOnAction(e -> showTable(1,null));
        Button godkButton = new Button("Beställda");
        godkButton.setOnAction(e -> showTable(2,null));
        Button levButton = new Button("Levererade");
        levButton.setOnAction(e -> showTable(3,null));

        //lägger adminknappar i horisontell låda
        adminButtons.getChildren().addAll(artButton,taBortArtButton);

        //lägger beställningsknappar i horisontell låda
        bestButtons.getChildren().addAll(laggBest, nyBestButton, attestButton, godkButton, levButton);
        bp = new BorderPane();
        bp.setTop(upperButtons);
        VBox temp = new VBox(10);
        temp.getChildren().addAll(adminButtons,artMeny);
        BorderPane.setMargin(temp,new Insets(0, 0, 0, 10));
        bp.setCenter(temp);
        bp.setStyle("-fx-background-color: #FFFFFF;");
        Scene adminScene = new Scene(bp, 1240, 620);
        adminScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(adminScene);
        primaryStage.show();


    }
    private void setRapp(){
        fullSearch = new VBox(5);
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
        DatePicker dp = new DatePicker();//från
        dp.setMaxWidth(100);
        DatePicker dtp = new DatePicker();//till
        dtp.setMaxWidth(100);
        Button srch = new Button("Filtrera");
        LocalDate now = LocalDate.now();
        LocalDate epoch = LocalDate.ofEpochDay(0);

        srch.setOnAction(e->{

            int from = (int)ChronoUnit.DAYS.between(epoch, now)-30, to = (int) ChronoUnit.DAYS.between(epoch, now);
            try{
                from = (int)dp.getValue().toEpochDay();
                to = (int)dtp.getValue().toEpochDay();
            }catch(NullPointerException ex){
                System.out.println(ex.getMessage());
            }

            ObservableList<Article> data = db.getSearchData(levVal.getText().trim(),nameVal.getText().trim(),nrVal.getText().trim(),from,to);

            showTable(4, data);
        });
        search.getChildren().addAll(new Text("  Leverantör: " ), levVal, new Text("Namn: " ), nameVal, new Text("Nummer: "), nrVal, new Text("Från: "),dp, new Text("Till: "),dtp,srch);
        fullSearch.getChildren().addAll(new Text("  Filtrera: "),search);
        int from = (int)ChronoUnit.DAYS.between(epoch, now)-30, to = (int) ChronoUnit.DAYS.between(epoch, now)+1;
        ObservableList<Article> data = db.getSearchData("","","",from,to);
        showTable(4,data);
    }
    private GridPane createArtMeny(){
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);


        ObservableList<Object> levArtOptions = db.getLevOnly();
        ComboBox<Object> levCombo = new ComboBox<>(levArtOptions);
        levCombo.setEditable(true);
        TextField prodValArtTx = new TextField("");
        prodValArtTx.setEditable(true);

        TextField prodNrValArtTx = new TextField("");
        prodNrValArtTx.setEditable(true);

        Button skapaArt = new Button("Lägg till artikel");
        skapaArt.setOnAction(e->{
            String lev = (String) levCombo.getValue();
            lev = lev.trim();
            String prod =  prodValArtTx.getText();
            prod = prod.trim();
            String nr = prodNrValArtTx.getText();
            nr = nr.trim();

            if(db.createArticle(lev, prod, nr)){
                AlertBox.display("Meddelande","Artikel tillagd");
            }
            else{
                AlertBox.display("Meddelande","Artikel kunde inte läggas till");
            }

            levCB.setItems(db.getLevOptions());
            levArtCB.setItems(db.getLevOptions());
            levCombo.setItems(db.getLevOnly());
            levCombo.setValue("");
            prodValArtTx.setText("");
            prodNrValArtTx.setText("");

        });
        gp.add(new Text("  Leverantör: "),0,0);
        gp.add(new Text("  Produkt: "),0, 1);
        gp.add(new Text("  Produktummer: "),0, 2);
        gp.add(levCombo,1,0);
        gp.add(prodValArtTx,1,1);
        gp.add(prodNrValArtTx,1,2);
        gp.add(skapaArt, 1,3);

        return gp;
    }
    private GridPane createLaggMeny(){
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);
        ChoiceBox<Object> prodCB = new ChoiceBox<>();
        ObservableList<Object> levOptions = db.getLevOptions();
        levCB = new ChoiceBox<>(levOptions);
        levCB.setOnAction(e-> prodCB.setItems(db.getProdOptions((String)levCB.getValue())));

        TextField nrTx = new TextField();
        nrTx.setEditable(false);
        prodCB.setOnAction(e-> {
            db.getProdNr((String) levCB.getValue(), (String) prodCB.getValue());
            nrTx.setText(db.getProdNr((String) levCB.getValue(), (String) prodCB.getValue()));
        });

        TextField prisTx = new TextField();
        prisTx.setText("");

        TextField projCB = new TextField();
        projCB.setText("3000");
        projCB.setEditable(true);
        ObservableList<String> prioOptions =
                FXCollections.observableArrayList(
                        "Normal",
                        "Hög prioritet"
                );
        ComboBox<String> prioCB = new ComboBox<>(prioOptions);
        prioCB.setValue("Normal");
        prioCB.setEditable(false);
        CheckBox chem = new CheckBox();

        Button skapaBest = new Button("Lägg beställning");
        skapaBest.setOnAction(e ->{
            if(confirmOrder((String)levCB.getValue(), (String)prodCB.getValue(), nrTx.getText(),prisTx.getText(), projCB.getText(), prioCB.getValue(), chem)){
                AlertBox.display("Meddelande","Beställning lagd. ");
            }else{
                AlertBox.display("Meddelande","Beställning kunde inte läggas. ");
            }

            prisTx.setText(" ");
            projCB.setText("3000");
            prioCB.setValue("Normal");
            chem.setSelected(false);
        } );
        gp.add(new Text("  Leverantör: "),0,0);
        gp.add(new Text("  Produkt: "),0, 1);
        gp.add(new Text("  Produktummer: "),0, 2);
        gp.add(new Text("  Cirkapris: "),0, 3);
        gp.add(new Text("  Projekt: "),0, 4);
        gp.add(new Text("  Prioritet: "),0, 5);
        gp.add(new Text("  Ny kemikalie: "),0, 6);
        gp.add(levCB,1,0);
        gp.add(prodCB,1,1);
        gp.add(nrTx,1,2);
        gp.add(prisTx,1,3);
        gp.add(projCB,1,4);
        gp.add(prioCB,1,5);
        gp.add(chem,1,6);
        gp.add(skapaBest, 1,7);

        return gp;
    }
    private GridPane createTaBortMeny(){
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);
        ObservableList<Object> levArtOptions = db.getLevOptions();

        levArtCB = new ChoiceBox<>(levArtOptions);
        ChoiceBox<Object> prodCB = new ChoiceBox<>();
        levArtCB.setOnAction(e-> prodCB.setItems(db.getProdOptions((String)levArtCB.getValue())));

        TextField nrTx = new TextField();
        nrTx.setEditable(false);
        prodCB.setOnAction(e-> nrTx.setText(db.getProdNr((String)levArtCB.getValue(),(String)prodCB.getValue())));

        Button taBortArt = new Button("Ta bort artikel");
        taBortArt.setOnAction(e->{
            if(db.removeArticle((String)levArtCB.getValue(), (String)prodCB.getValue(), nrTx.getText() )){
                AlertBox.display("Meddelande","Artikel borttagen");
                levArtCB.setItems(db.getLevOptions());
                levArtCB.setValue("");
                prodCB.setValue("");
                nrTx.setText("");
            }
            else{
                AlertBox.display("Meddelande","Artikel kunde inte tas bort");
            }

        });

        gp.add(new Text("  Leverantör: "),0,0);
        gp.add(new Text("  Produkt: "),0, 1);
        gp.add(new Text("  Produktummer: "),0, 2);
        gp.add(levArtCB,1,0);
        gp.add(prodCB,1,1);
        gp.add(nrTx,1,2);
        gp.add(taBortArt,1,3);

        return gp;
    }
    private GridPane createAddAnvMeny(){
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);
        TextField anvValTx = new TextField();
        anvValTx.setEditable(true);
        PasswordField pwValTx = new PasswordField();
        pwValTx.setEditable(true);
        CheckBox admin = new CheckBox();
        CheckBox cost = new CheckBox();


        Button skapaAnv = new Button("Lägg till användare");
        skapaAnv.setOnAction(e->{
            if(!currentUser.isOrderAdmin()){
                AlertBox.display("Meddelande","Adminrätt krävs för denna åtgärd. ");
            }else{
                if((admin.isSelected() || cost.isSelected()) && !currentUser.isFullAdmin()){
                    AlertBox.display("Meddelande","Adminrätt krävs för denna åtgärd. ");
                }else if(db.createUser(anvValTx.getText(),pwValTx.getText(),admin.isSelected(),cost.isSelected())){
                    AlertBox.display("Meddelande","Användare tillagd");
                    anvValTx.setText("");
                    pwValTx.setText("");
                    admin.setSelected(false);
                    cost.setSelected(false);
                }
                else{
                    AlertBox.display("Meddelande","Användarnamn upptaget");
                }
            }



        });
        gp.add(new Text("  Användarnamn: "),0,0);
        gp.add(new Text("  Lösenord: "),0, 1);
        gp.add(new Text("  Adminrätt: "),0, 2);
        gp.add(new Text("  Beställrätt: "),0, 3);
        gp.add(anvValTx,1,0);
        gp.add(pwValTx,1,1);
        gp.add(admin,1,2);
        gp.add(cost,1,3);
        gp.add(skapaAnv,1,4);

        return gp;
    }
    private GridPane createTaBortAnvMeny(){
        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);

        TextField anvValTx = new TextField();
        anvValTx.setEditable(true);

        PasswordField pwValTx = new PasswordField();
        pwValTx.setEditable(true);

        Button taBortAnv = new Button("Ta bort användare");
        taBortAnv.setOnAction(e->{
            if(!currentUser.isFullAdmin()){
                AlertBox.display("Meddelande","Adminrätt krävs för denna åtgärd. ");
            }else if(db.deleteUser(anvValTx.getText(),pwValTx.getText())){
                AlertBox.display("Meddelande","Användare borttagen");
                anvValTx.setText("");
                pwValTx.setText("");
            }
            else{
                AlertBox.display("Meddelande","Ett fel uppstod. \n Användaren fanns inte. ");
            }

        });

        gp.add(new Text("  Användarnamn: "),0,0);
        gp.add(new Text("  Lösenord: "),0, 1);
        gp.add(anvValTx,1,0);
        gp.add(pwValTx,1,1);
        gp.add(taBortAnv,1,2);

        return gp;
    }
    private boolean confirmOrder(String lev, String prod, String nr, String pris, String proj, String prio, CheckBox chem){
        String chemText = " ";
        if(chem.isSelected()){
            chemText = "Ny kemikalie";
        }
        return db.addBest(lev,prod,nr,pris,proj,prio,chemText,currentUser.getName()); //läggbest
    }
    private void showTable(int i, ObservableList<Article> dataI){

        /*
        Hämtar data ifrån databasen. Skapar sedan kolumner för tabellen och gör en tabell.
        Utnyttjar klassen buttoncell för knapparna i tabellen.
        */
        TableView<Article> tab = new TableView<>();
        tab.setMaxHeight(385); //TODO fixa proper height för tabell??? eventuellt i insets

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
        col_action.setCellFactory(e-> new ButtonCell(i));

        if(i==0){
            tab.getColumns().addAll(levCol, nameCol, prisCol, projCol, chemCol, dateCol,col_action);
        }else if(i==1){
            tab.getColumns().addAll(levCol, nameCol, nrCol, prioCol,userCol, dateCol,col_action);
        }else if(i==2){
            tab.getColumns().addAll(levCol, nameCol, nrCol, userCol, bestCol, col_kyl, col_action);
        }else if(i==3){
            tab.getColumns().addAll(levCol, nameCol, nrCol, prioCol,userCol, dateCol, col_kylRes, col_action);
        }else if(i==4){
            tab.getColumns().addAll(levCol, nameCol, nrCol,prisCol,projCol, prioCol,userCol, dateCol, recCol, col_kylRes);
        }

        VBox temp = new VBox(10);
        if(dataI == null){
            data = db.getTable(i);
            temp.getChildren().addAll(bestButtons);

        }else{
            data = dataI;
            temp.getChildren().addAll(fullSearch);
        }

        tab.setItems(data);
        temp.getChildren().addAll(tab);
        bp.setCenter(temp);

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
        ButtonCell(int a){
            String name = "";
            boolean b = true;
            if(a==0){
                name = "Godkänn";
                if(!currentUser.isFullAdmin()) {
                    b = false;
                }
            }else if(a==1){
                name = "Beställd";
                if(!currentUser.isOrderAdmin()) {
                    b = false;
                }
            }else if(a==2){
                name = "Levererad";
            }
            cellButton.setText(name);
            boolean finalB = b;
            cellButton.setOnAction(e->{
                if(!finalB){
                    AlertBox.display("Meddelande","Adminrätt krävs. ");
                }else{
                    int i = getTableRow().getIndex();
                    ObservableList<Article> data = getTableView().getItems();
                    Article selArt = data.get(i);
                    db.orderAccepted(selArt.getTable(), selArt.getID());
                    showTable(a,null);
                }

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

