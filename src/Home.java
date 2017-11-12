import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * Provides a nice visual for users. Regular users can use the RCMs while admins can view the
 * RMOS window after first logging in.
 */

public class Home extends JFrame implements ActionListener {

    JDBCHandler jdbc_handler;

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    private static Container pane;

    // NOTE*** Maps mean it is static objects
    // NOTE*** ArrayList means that It will be dynamic (gathered from database)
    private Map<String, JButton> buttons;
    /*  List of Buttons:
            search
            close
    */

    private Map<String, JLabel> labels;
    /*  List of Buttons:
            title
            day_of_week
            start_time
            end_time
            attributes
    */
    private Map<String, JComboBox> drop_downs;
    /*  List of Drop downs:
            day_of_week
            start_time
            end_time
            attributes
     */

    private Map<String, JScrollPane> scroll_panes;
    /*  List of Scroll Panes:
            categories
            main_category
            attributes
     */

    /*  Description
        Services -- far left column i.e. Restaurants, Sports
        Categories -- middle column i.e. Mexican, Asian
        Options -- right column i.e. price range
     */

    //TODO THESE STRINGS SHOULD BE CREATED DYNAMICALLY
    private String[] string_days_of_week = {"N/A", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String[] string_hours_of_day =
            {"N/A", "12:00AM", "1:00AM", "2:00AM", "3:00AM", "4:00AM", "5:00AM", "6:00AM", "7:00AM", "8:00AM", "9:00AM", "10:00AM", "11:00AM",
                    "12:00PM", "1:00PM", "2:00PM", "3:00PM", "4:00PM", "5:00PM", "6:00PM", "7:00PM", "8:00PM", "9:00PM", "10:00PM", "11:00PM"
            };

    private String[] main_business_categories = {
            "Active Life", "Arts and Entertainment", "Automotive", "Car Rental", "Cafe",
            "Beauty and Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores",
            "Department Stores", "Education", "Event Planning and Services", "Flowers and Gifts",
            "Food", "Health and Medical", "Home Services", "Home and Garden", "Hospitals", "Hotels and Travel",
            "Hardware Stores", "Grocery", "medical Centers", "Nurseries and Gardening", "Nightlife",
            "Restaurants", "Shopping", "Transportation"
    };

    private String[] sub_business_categories = {};

    private String[] attributes = {};

    private String[] result_columns = {"Business", "City", "State", "Stars"};

    Object[][] data = new Object[][]{};

    String[] data_ids;


    Object[][] reviews_data = new Object[][]{};


    //These Variables are controlled by the GUI and will alter the final search results -- builder blocks for SQL query
    private Set<String> main_category_set = new HashSet<>();

    private Set<String> subcategory_set = new HashSet<>();

    private Set<String> attributes_set = new HashSet<>();

    private String day_of_week = "";
    private String start_time = "";
    private String end_time = "";
    private String search_attribute = "";
    private StringBuilder business_id_requested = new StringBuilder();
    private StringBuilder review_business_name = new StringBuilder();


    private int open_every_other_counter = 0; //TODO THIS IS A HOT FIX FOR OPENING REVIEWS
    //Reviews currently open up two every click? this toggle will allow one . %2


    public Home(JDBCHandler jdbc_handler) {
        this.jdbc_handler = jdbc_handler;
        //Gui Stuff
        JFrame frame = new JFrame("Home Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();

        //Size and display the window.
        Insets frameInsets = frame.getInsets();
        frame.setSize(WINDOW_WIDTH + frameInsets.left + frameInsets.right,
                WINDOW_HEIGHT + frameInsets.top + frameInsets.bottom);
        frame.setVisible(true);
        pane.setLayout(null);


        //Initialize Components
        buttons = new HashMap<>();
        labels = new HashMap<>();
        drop_downs = new HashMap<>();
        buttons = new HashMap<>();
        scroll_panes = new HashMap<>();

        createLabels();
        createButtons();
        createDropDowns();
        createScrollPanes();

    }

    /**
     *
     */
    public void createLabels() {
        // Creating Labels
        labels.put("title", GeneralJStuff.createLabel(pane, "Yelp System", 475, 10));
        labels.put("day_of_week", GeneralJStuff.createLabel(pane, "Day of the Week", 50, 500));
        labels.put("start_time", GeneralJStuff.createLabel(pane, "From:", 200, 500));
        labels.put("end_time", GeneralJStuff.createLabel(pane, "To:", 350, 500));
        labels.put("attributes", GeneralJStuff.createLabel(pane, "Search For:", 500, 500));

    }

    /**
     *
     */
    public void createButtons() {
        Runnable r_search = new Runnable() {
            @Override
            public void run() {
                queryFindBusiness.run();
                scroll_panes.get("results").setVisible(false);
                pane.remove(scroll_panes.get("results"));
                scroll_panes.put("results", GeneralJStuff.createTableScrollPane(pane, result_columns, data,data_ids, 500, 50, 450, 400, business_id_requested,review_business_name, createReviews ));

            }
        };
        Runnable r_close = new Runnable() {
            @Override
            public void run() {
                jdbc_handler.closeConnection();
                System.exit(0);
            }
        };

        buttons.put("search", GeneralJStuff.createButton(pane, "Search", 700, 500, 100, 25, r_search));
        buttons.put("close", GeneralJStuff.createButton(pane, "Close", 850, 500, 100, 25, r_close));
    }

    /**
     *
     */
    public void createDropDowns() {
        // Creating Drop Downs
        Runnable r_day_of_week = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("day_of_week").getSelectedItem());
                day_of_week = drop_downs.get("day_of_week").getSelectedItem().toString();
            }
        };

        Runnable r_start_time = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("start_time").getSelectedItem());
                start_time = drop_downs.get("start_time").getSelectedItem().toString();
            }
        };
        Runnable r_end_time = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("end_time").getSelectedItem());
                end_time = drop_downs.get("end_time").getSelectedItem().toString();
            }
        };
        Runnable r_attributes = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("attributes").getSelectedItem());
                search_attribute = drop_downs.get("attributes").getSelectedItem().toString();
            }
        };
        drop_downs.put("day_of_week", GeneralJStuff.createDropDown(pane, string_days_of_week, 50, 500, 100, 100, r_day_of_week));
        drop_downs.put("start_time", GeneralJStuff.createDropDown(pane, string_hours_of_day, 200, 500, 100, 100, r_start_time));
        drop_downs.put("end_time", GeneralJStuff.createDropDown(pane, string_hours_of_day, 350, 500, 100, 100, r_end_time));
        drop_downs.put("attributes", GeneralJStuff.createDropDown(pane, string_hours_of_day, 500, 500, 100, 100, r_attributes));

    }

    /**
     *
     */
    public void createScrollPanes() {
        scroll_panes.put("main_category", GeneralJStuff.createCheckBoxScrollPane(pane, main_business_categories, 50, 50, 145, 400, main_category_set, queryFindTypes));
        scroll_panes.put("sub_category", GeneralJStuff.createCheckBoxScrollPane(pane, sub_business_categories, 200, 50, 145, 400, subcategory_set, queryFindTypes));
        scroll_panes.put("attributes", GeneralJStuff.createCheckBoxScrollPane(pane, attributes, 350, 50, 145, 400, attributes_set, queryFindTypes));
        scroll_panes.put("results", GeneralJStuff.createTableScrollPane(pane, result_columns, data,data_ids, 500, 50, 450, 400, business_id_requested,review_business_name, createReviews ));
    }


    /**
     * I guess this doesn't really need to be here.... but I just inherited the class... so I'm just leaving it blank :)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void updateDropDown(String drop_down_key) {

        String[] array = {"N/A", "test"}; //TODO CHANGE THIS TO BE FROM DB QUERY .. REMEMBER TO HAVE N/A IN THE BEGINNING
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get(drop_down_key).getSelectedItem());    //TODO CHANGE THIS
            }
        };
        drop_downs.get(drop_down_key).setVisible(false);
        pane.remove(drop_downs.get(drop_down_key));
        drop_downs.put("attributes", GeneralJStuff.createDropDown(pane, array, 500, 500, 100, 100, r));

    }

    /**
     * Triggered by Search Button
     */
    Runnable queryFindBusiness = new Runnable() {
        @Override
        public void run() {
            String search_query = "SELECT DISTINCT b.name, b.city, b.state, b.review_count, b.business_id " +
                    "FROM Business b " +
                    "INNER JOIN MainCategories mc ON b.business_id=mc.business_id " +
                    "INNER JOIN SubCategories sc ON b.business_id=sc.business_id " +
                    "INNER JOIN Attributes a ON b.business_id=a.business_id ";

            if (main_category_set.size() != 0) {
                search_query += "WHERE ";
            }

            // Adding Main Category Where Conditions
            search_query += "( ";
            search_query += createCategoriesString();
            search_query += ") ";

            // Adding Sub Categories Where Conditions
            if(subcategory_set.size() > 0) {
                search_query += " AND (";
                search_query += createSubcategoriesString();
                search_query += ")";
            }

            // Adding Attributes Where Conditions
            if(attributes_set.size() > 0) {
                search_query += " AND (";
                search_query += createAttributesString();
                search_query += ")";
            }

            // Querying Database
            ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 4);
            System.out.println(Arrays.toString(data));
            data = jdbc_handler.arrayListToObjectArray(results);

            // Does it again to get business IDs (used as reference for Reviews)
            results = jdbc_handler.makeSearchQuery(search_query, 5);

            data_ids = new String[results.size()];
            for(int i=0; i<results.size(); i++){
                data_ids[i] = results.get(i)[4];
            }
            System.out.println(Arrays.toString(data_ids));
        }
    };



    // Second Column: Based off of Category
    Runnable queryFindTypes = new Runnable() {
        @Override
        public void run() {
            if(main_category_set.size() == 0){
                attributes = new String[0];
                sub_business_categories = new String[0];

                scroll_panes.get("sub_category").setVisible(false);
                pane.remove(scroll_panes.get("sub_category"));
                scroll_panes.put("sub_category", GeneralJStuff.createCheckBoxScrollPane(pane, sub_business_categories, 200, 50, 145, 400, subcategory_set, queryFindTypes));

                scroll_panes.get("attributes").setVisible(false);
                pane.remove(scroll_panes.get("attributes"));
                scroll_panes.put("attributes", GeneralJStuff.createCheckBoxScrollPane(pane, attributes, 350, 50, 145, 400, attributes_set, queryFindTypes));

            }else {

                String search_query = "SELECT DISTINCT(sc.category) FROM SubCategories sc JOIN ";
                search_query +=
                        "( SELECT b.business_id as b_id " +
                                "FROM Business b " +
                                "INNER JOIN MainCategories mc ON b.business_id=mc.business_id ";
                search_query += "WHERE ";

                search_query += createCategoriesString();

                search_query += ")query  ON query.b_id=sc.business_id";


                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 1);
                String[] string_results = jdbc_handler.arrayListToStringArray(results);
                System.out.println(Arrays.toString(string_results));

                Set<String> temp = new HashSet<>(Arrays.asList(string_results));
                temp.removeAll(Arrays.asList(main_business_categories));


                sub_business_categories = temp.toArray(new String[temp.size()]);

                System.out.println(Arrays.toString(sub_business_categories));

                scroll_panes.get("sub_category").setVisible(false);
                pane.remove(scroll_panes.get("sub_category"));
                scroll_panes.put("sub_category", GeneralJStuff.createCheckBoxScrollPane(pane, sub_business_categories, 200, 50, 145, 400, subcategory_set, queryFindAttributes));
            }
        }
    };

    // Second Column: Based off of Category
    Runnable queryFindAttributes = new Runnable() {
        @Override
        public void run() {
            String search_query = "SELECT DISTINCT(att.attribute)so FROM Attributes att JOIN ";
            search_query+=
                    "( SELECT b.business_id as b_id, mc.category as mc_category, sc.category as sc_category " +
                            "FROM Business b " +
                            "INNER JOIN MainCategories mc ON b.business_id=mc.business_id " +
                            "INNER JOIN SubCategories sc ON b.business_id=sc.business_id ";


            if (main_category_set.size() != 0){
                search_query += "WHERE (";
            }

            search_query += createCategoriesString(); // TODO CHECK THIS

            search_query += ") AND (";

            search_query += createSubcategoriesString();

            search_query += ") )query  ON query.b_id=att.business_id";

            ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 1);
            String[] string_results = jdbc_handler.arrayListToStringArray(results);
            attributes = string_results;
//            System.out.println(Arrays.toString(string_results));
//
//            Set<String> temp = new HashSet<>(Arrays.asList(string_results));
//            temp.removeAll(Arrays.asList(main_business_categories));
//
//
//            attributes = temp.toArray(new String[temp.size()]);

            System.out.println(Arrays.toString(attributes));

            scroll_panes.get("attributes").setVisible(false);
            pane.remove(scroll_panes.get("attributes"));
            scroll_panes.put("attributes", GeneralJStuff.createCheckBoxScrollPane(pane, attributes, 350, 50, 145, 400, attributes_set, r_empty));
        }
    };

    Runnable r_empty = new Runnable() {
        @Override
        public void run() {

        }
    };



    // Second Column: Based off of Category
    Runnable createReviews = new Runnable() {
        @Override
        public void run() {
            if (open_every_other_counter++ %2 == 0) {//TODO git staHOT FIX
                String search_query = "SELECT r.date_string, r.stars, r.text, r.user_id, r.v_useful " +
                        "FROM Business b " +
                        "INNER JOIN Review r ON b.business_id=r.business_id " +
                        "WHERE  r.business_id=" + addQuotes(business_id_requested.toString());

                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 5);
                Object[][] obj_results = jdbc_handler.arrayListToObjectArray(results);
                System.out.println(Arrays.toString(obj_results));

                reviews_data = obj_results;

                new Reviews(review_business_name.toString(), reviews_data);
            }

        }
    };


    // ------------------------- Miscellaneous ----------------------- //

    private String addQuotes(String s){
        return "'" + (s) + "'";
    }


    private String createCategoriesString(){
        String string = "";
        int counter = 0;
        for (String str : main_category_set) {
            string += " mc.category=";
            string += addQuotes(str);
            if (counter++ != main_category_set.size() - 1) {
                string += " OR ";
            }
        }
        return string;
    }

    private String createSubcategoriesString(){
        String string = "";
        int counter = 0;
        for (String str : subcategory_set) {
            string += " sc.category=";
            string += addQuotes(str);
            if (counter++ != subcategory_set.size() - 1) {
                string += " OR ";
            }
        }
        return string;
    }

    private String createAttributesString(){
        int counter = 0;
        String string = "";
        for (String str : attributes_set){
            string += " a.attribute=";
            string += addQuotes(str);
            if (counter++ != attributes_set.size() - 1) {
                string += " OR ";
            }
        }
        return string;
    }



}