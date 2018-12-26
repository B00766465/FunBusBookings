package com.michael;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.shared.ui.ContentMode;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
                
        Connection connection = null;
        String connectionString = "jdbc:sqlserver://db4class.database.windows.net:1433;"+
                                  "database=TestingAzureDatabases;user=michael@db4class;"+
                                  "password=CloudDev123;encrypt=true;"+
                                  "trustServerCertificate=false;"+
                                  "hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
      

                final VerticalLayout layout = new VerticalLayout();
        
        try{
            connection = DriverManager.getConnection(connectionString);
            //layout.addComponent(new Label("Connected to database"+ connection.getCatalog()));
                        // Execute a query against the database and return rows to the ResultSet using the column names (these must be strings "") in the Schema.sql
                        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM BUS_DETAILS");

                        List<Bus> buses = new ArrayList<Bus>();//Import List java.util.ArrayList; and import java.util.List;
                        while(rs.next()){
                                        buses.add(new Bus(rs.getString("Destination"),
                                        rs.getInt("Capacity"),
                                        rs.getString("Feature"),
                                        rs.getString("Accessibility")));
                        }//while           

            // Convert the resultset that comes back into a List - we need a Java class to represent the data (Room.java in this case)
            

            // Add my component
            Grid<Bus> myGrid = new Grid<>();//Import com.vaadin.ui.Grid;
            // Set the items (List)
            myGrid.setItems(buses);
            // Configure the order and the caption of the grid
            myGrid.addColumn(Bus::getDestination).setCaption("Destination");
            myGrid.addColumn(Bus::getCapacity).setCaption("Capacity");
            myGrid.addColumn(Bus::getFeature).setCaption("Feature");
            myGrid.addColumn(Bus::getAccessibility).setCaption("Accessibility");
            myGrid.setSelectionMode(SelectionMode.MULTI);//Import com.vaadin.ui.Grid.SelectionMode;
            myGrid.setSizeFull();
            MultiSelect<Bus> selected = myGrid.asMultiSelect();//import com.vaadin.ui.MultiSelect;

    //Adding the logo and Student Number
	Label logo = new Label("<H1>Fun Bus Bookings</H1> <p/> <h3>Please enter the details below and click Book</h3>", ContentMode.HTML);
    //Label studentNo = new Label("B00766465"); //This is one option

    //Add a horizontal Layout
    final HorizontalLayout hLayout = new HorizontalLayout();
    TextField name = new TextField("Name of party");
        
    //Add a slider
    Slider mySlider = new Slider("How many people", 0, 300);
    mySlider.setValue(0.0);
    mySlider.setWidth("500px");//Could also use people.setWidth("500px") 
        
    //Add a combo box
    ComboBox<String> accessible = new ComboBox<>("Accessibility");
    accessible.setItems("Accessible","Not Accessible");

    //Add a button
    Button button = new Button("Book");
    Label screenMessage = new Label("Your booking is not complete yet");
    screenMessage.setContentMode(ContentMode.HTML);//Applies formatting to the message associated with clicking the button hereafter

    //Adding click listener
    button.addClickListener(e -> {

        String aString = selected.getValue().stream().map(Bus::getAccessibility).collect(Collectors.joining(","));
        int cap = selected.getValue().stream().mapToInt(Bus::getCapacity).sum();
        screenMessage.setValue(String.valueOf(cap));
        String match = "Not Accessible";

        //If the user doesn't select a bus print this message
        if (myGrid.getSelectedItems().size() == 0) {
            screenMessage.setValue("<strong>Please select at least one bus</strong>");
        } else if (name.isEmpty()) {
            screenMessage.setValue("<strong>Please enter group name.</strong>");
        } else if (accessible.isEmpty()) {
           screenMessage.setValue("<strong>Please confirm if you need an accessible bus</strong>");
        } else if ((accessible.getValue() == "Accessible") && (aString.equalsIgnoreCase(match))) {
            screenMessage.setValue("<strong>You cannot select a non-accessible bus.</strong>");
        } else if (mySlider.getValue().intValue() > cap) {
            screenMessage.setValue("<strong>You have selected buses with a max capacity of " + cap
                    + " which is not enough to hold </strong>" + mySlider.getValue().intValue());
        } else {
            screenMessage.setValue("<strong>Success! The group is booked now</strong>");
           }//if
        });//button



    //Defining the final layout
    hLayout.addComponents(name, mySlider,accessible);
    layout.addComponents(logo, hLayout, button, screenMessage, myGrid, new Label("B00766465"));
    

        }//try
        catch (Exception e){
            layout.addComponent(new Label(e.getMessage()));
        }//catch
        
        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
