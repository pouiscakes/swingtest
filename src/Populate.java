import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Populate {

    JDBCHandler jdbc_handler;

    private String[] mbc = {
            "Active Life", "Arts and Entertainment", "Automotive", "Car Rental", "Cafe",
            "Beauty and Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores",
            "Department Stores", "Education", "Event Planning and Services", "Flowers and Gifts",
            "Food", "Health and Medical", "Home Services", "Home and Garden", "Hospitals", "Hotels and Travel",
            "Hardware Stores", "Grocery", "medical Centers", "Nurseries and Gardening", "Nightlife",
            "Restaurants", "Shopping", "Transportation"
    };

    private ArrayList<String> main_business_categories = new ArrayList<>(Arrays.asList(mbc));

    public static void main(String[] args) { //TODO CHECK THE INPUTS TO HAVE THE 4 json files
        JDBCHandler jdbc_handler = new JDBCHandler();

        Populate reader = new Populate(jdbc_handler);
//        reader.read_file();
//        reader.view_all_file_keys();

        //The Order matters here! Look at createdb so see the dependencies (foreign keys)

        //TODO REMOVE THIS LINE
        args = new String[]{"yelp_business.json"};

        // Populates Business Table
        if (Arrays.asList(args).contains("yelp_business.json")) {
            System.out.println("Populating Business Table");
            reader.read_json_file("yelp_business.json");
        }

        if (Arrays.asList(args).contains("yelp_user.json")) {
            System.out.println("Populating YelpUser Table");
            reader.read_json_file("yelp_user.json");
        }

        if (Arrays.asList(args).contains("yelp_review.json")) {
            System.out.println("Populating Review Table");
            reader.read_json_file("yelp_review.json");
        }

        if (Arrays.asList(args).contains("yelp_checkin.json")) {
            reader.read_json_file("yelp_checkin.json");
        }

        jdbc_handler.closeConnection();
    }

    public Populate(JDBCHandler jdbc_handler) {
        this.jdbc_handler = jdbc_handler;
    }

    private void view_all_file_keys() {
        System.out.println("---------------------- Yelp Business Keys----------------------");
        view_json_object_keys("yelp_business.json");

        System.out.println("---------------------- Yelp User Keys----------------------");
        view_json_object_keys("yelp_user.json");

        System.out.println("---------------------- Yelp Review Keys----------------------");
        view_json_object_keys("yelp_review.json");

        System.out.println("---------------------- Yelp Checkin Keys----------------------");
        view_json_object_keys("yelp_checkin.json");
    }

    /**
     * IMPORTANT: File Path & directory matters!
     * Takes in one of four files:
     * yelp_business.json
     * yelp_review.json
     * yelp_checkin.json
     * yelp_user.json
     * These files should be in folder "YelpDataset" at the root of the project (equivalent with src folder)
     * <p>
     * This will read the file line by line and write it into the database
     */
    private void read_file() {

        JSONParser parser = new JSONParser();
        try {
            File file = new File("YelpDataset/yelp_business.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            Set<String> overall = new HashSet<>();
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject jsonObj = (JSONObject) parser.parse(line);
                System.out.println(jsonObj.get("attributes"));

//                Set<String> test = ((JSONObject)jsonObj.get("attributes")).keySet();
//                overall.addAll(test);
//                System.out.println("-------------");
//                System.out.println(overall);
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    //[Has TV, Coat Check, Open 24 Hours, Accepts Insurance, Alcohol, Dogs Allowed, Caters, Price Range, Happy Hour, Good for Kids, Good For Dancing, Outdoor Seating, Good For Kids, Takes Reservations, Waiter Service, Wi-Fi, Good For, Parking, Hair Types Specialized In, Drive-Thru, Order at Counter, Accepts Credit Cards, BYOB/Corkage, Good For Groups, Noise Level, By Appointment Only, Take-out, Wheelchair Accessible, BYOB, Music, Attire, Payment Types, Delivery, Ambience, Dietary Restrictions, Corkage, Ages Allowed, Smoking]

    /**
     * IMPORTANT: File Path & directory matters!
     * Takes in one of four files:
     * yelp_business.json
     * yelp_review.json
     * yelp_checkin.json
     * yelp_user.json
     * These files should be in folder "YelpDataset" at the root of the project (equivalent with src folder)
     * <p>
     * This will read the file line by line and write it into the database
     *
     * @param file_name
     */
    private void read_json_file(String file_name) {

        JSONParser parser = new JSONParser();
        try {
            File file = new File("YelpDataset/" + file_name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject jsonObj = (JSONObject) parser.parse(line);
                if (file_name.equals("yelp_business.json")) {
                    insert_to_business(jsonObj);
                } else if (file_name.equals("yelp_checkin.json")) {
                    insert_to_checkin(jsonObj);
                } else if (file_name.equals("yelp_review.json")) {
                    insert_to_review(jsonObj);
                } else if (file_name.equals("yelp_user.json")) {
                    insert_to_user(jsonObj);
                } else {
                    throw new Exception("json file not found");
                }
//                break;  //TODO REMOVE ME REMOVE ME REMOVE ME REMOVE ME REMOVE ME REMOVE ME REMOVE ME REMOVE ME REMOVE ME
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * IMPORTANT: File Path & directory matters!
     * Takes in one of four files:
     * yelp_business.json
     * yelp_review.json
     * yelp_checkin.json
     * yelp_user.json
     * These files should be in folder "YelpDataset" at the root of the project (equivalent with src folder)
     * <p>
     * This will read the first entry (line) of the file and output the keys
     *
     * @param file_name
     */
    private void view_json_object_keys(String file_name) {
        JSONParser parser = new JSONParser();
        try {
            File file = new File("YelpDataset/" + file_name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            JSONObject jsonObj = (JSONObject) parser.parse(line);
            Set<String> test = jsonObj.keySet();
            System.out.println(test);
            for (String item : test) {
                System.out.println(item + " --- " + jsonObj.get(item));
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will return the input with as a string surrounded by quotes
     * i.e.
     * input: hello world
     * output: "hello world"
     * this will be primarily used with inserting values into the database to explicitly note a value as a string
     */
    private String add_quotes(String s) {
        return "'" + sanitize_string(s) + "'";
    }

    private String remove_last_character(String s) {
        return s.substring(0, s.length() - 1);
    }

    private String sanitize_string(String s) {
        s = s.replaceAll("[\n\r]", " ");
        s = s.replaceAll("'", "");
        return s.replaceAll("&", "and"); //TODO & Symbols don't work during inserts into DB
    }

    private Boolean isBusinessCategory(String s) {
//        if (main_business_categories.contains(s)) {
//            return "true";
//        } else {
//            return "false";
//        }
        return main_business_categories.contains(s);
    }


    /**
     * This will covert the json object into an equivalent Database Query Insert String
     *
     * @param entry : "line" of the json input file
     * @return Database Query INSERT String
     */
    private String insert_to_user(JSONObject entry) {

        List<String> fields = Arrays.asList("user_id", "name", "yelping_since", "average_stars", "review_count", "type", "fans");
        List<String> vote_fields = Arrays.asList("cool", "useful", "funny");
        List<String> compliment_fields = Arrays.asList("note", "plain", "cool", "hot", "funny");//TODO CHANGE THIS


        JSONObject votes = (JSONObject) entry.get("votes");

        JSONObject compliments = (JSONObject) entry.get("compliments");

        String[] fields_result = new String[fields.size()];
        String[] votes_fields_result = new String[vote_fields.size()];
        String[] compliments_fields_result = new String[compliment_fields.size()];

        for (int i = 0; i < fields.size(); i++) {
//            System.out.println(fields.get(i) + entry.get(fields.get(i)));
            fields_result[i] = entry.get(fields.get(i)).toString();
        }

        for (int i = 0; i < vote_fields.size(); i++) {
//            System.out.println(vote_fields.get(i) + votes.get(vote_fields.get(i)));
            votes_fields_result[i] = votes.get(vote_fields.get(i)).toString();
        }

        for (int i = 0; i < compliment_fields.size(); i++) {
            try {
                compliments_fields_result[i] = compliments.get(compliment_fields.get(i)).toString();
            } catch (Exception e) {
                compliments_fields_result[i] = "0";
            }
        }


        String insert_string = "INSERT INTO YelpUser VALUES(";
        //user_id
        insert_string += add_quotes(fields_result[fields.indexOf("user_id")]);
        insert_string += ",";
        //name
        insert_string += add_quotes(fields_result[fields.indexOf("name")]);
        insert_string += ",";
        //yelping_since
        insert_string += add_quotes(fields_result[fields.indexOf("yelping_since")]);
        insert_string += ",";
        //average_stars
        insert_string += fields_result[fields.indexOf("average_stars")];
        insert_string += ",";
        //review_count
        insert_string += fields_result[fields.indexOf("review_count")];
        insert_string += ",";
        //type
        insert_string += add_quotes(fields_result[fields.indexOf("type")]);
        insert_string += ",";
        //fans
        insert_string += fields_result[fields.indexOf("fans")];
        insert_string += ",";

        //c_note
        insert_string += compliments_fields_result[compliment_fields.indexOf("note")];
        insert_string += ",";

        //c_plain
        insert_string += compliments_fields_result[compliment_fields.indexOf("plain")];
        insert_string += ",";

        //c_cool
        insert_string += compliments_fields_result[compliment_fields.indexOf("cool")];
        insert_string += ",";

        //c_hot
        insert_string += compliments_fields_result[compliment_fields.indexOf("hot")];
        insert_string += ",";

        //c_funny
        insert_string += compliments_fields_result[compliment_fields.indexOf("funny")];
        insert_string += ",";

        //v_cool
        insert_string += votes_fields_result[vote_fields.indexOf("cool")];
        insert_string += ",";
        //v_useful
        insert_string += votes_fields_result[vote_fields.indexOf("useful")];
        insert_string += ",";
        //v_funny
        insert_string += votes_fields_result[vote_fields.indexOf("funny")];
        insert_string += ")";

        System.out.println(insert_string);
        System.out.println(jdbc_handler.makeUpdateQuery(insert_string));
        return insert_string;
    }

    /**
     * This will covert the json object into an equivalent Database Query Insert String
     *
     * @param entry : "line" of the json input file
     * @return Database Query INSERT String
     */
    private String insert_to_business(JSONObject entry) {

        List<String> fields = Arrays.asList("business_id", "name", "full_address", "city", "state", "longitude", "latitude", "review_count",
                "stars", "type", "open");

        List<String> hours_fields = Arrays.asList("mon_open", "mon_close", "tue_open", "tue_close", "wed_open", "wed_close", "thu_open",
                "thu_close", "fri_open", "fri_close", "sat_open", "sat_close", "sun_open", "sun_close");

        JSONObject hours = (JSONObject) entry.get("hours");
        JSONObject[] day_of_week = new JSONObject[7];
        day_of_week[0] = (JSONObject) hours.get("Monday");
        day_of_week[1] = (JSONObject) hours.get("Tuesday");
        day_of_week[2] = (JSONObject) hours.get("Wednesday");
        day_of_week[3] = (JSONObject) hours.get("Thursday");
        day_of_week[4] = (JSONObject) hours.get("Friday");
        day_of_week[5] = (JSONObject) hours.get("Saturday"); //TODO CHECK IF THERES ARE THERE?
        day_of_week[6] = (JSONObject) hours.get("Sunday");


        JSONObject attributes = (JSONObject) entry.get("attributes");
        JSONArray categories = (JSONArray) entry.get("categories");


        String[] fields_result = new String[fields.size()];
        ArrayList<String> hour_fields_result = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            fields_result[i] = entry.get(fields.get(i)).toString();
        }

        for (int i = 0; i < day_of_week.length; i++) {
            try {
                hour_fields_result.add((day_of_week[i].get("open").toString()));
            } catch (NullPointerException e) {
                hour_fields_result.add("null");
            }
            try {
                hour_fields_result.add((day_of_week[i].get("close").toString()));
            } catch (NullPointerException e) {
                hour_fields_result.add("null");
            }
        }


        String insert_string = "INSERT INTO Business VALUES(";
        //business_id
        insert_string += add_quotes(fields_result[fields.indexOf("business_id")]);
        insert_string += ",";
        //name
        insert_string += add_quotes(fields_result[fields.indexOf("name")]);
        insert_string += ",";
        //full_address
        insert_string += add_quotes(fields_result[fields.indexOf("full_address")]);
        insert_string += ",";
        //city
        insert_string += add_quotes(fields_result[fields.indexOf("city")]);
        insert_string += ",";
        //state
        insert_string += add_quotes(fields_result[fields.indexOf("state")]);
        insert_string += ",";
        //longitude
        insert_string += add_quotes(fields_result[fields.indexOf("longitude")]);
        insert_string += ",";
        //latitude
        insert_string += add_quotes(fields_result[fields.indexOf("latitude")]);
        insert_string += ",";
        //review_count
        insert_string += fields_result[fields.indexOf("review_count")];
        insert_string += ",";
        //open
        insert_string += fields_result[fields.indexOf("stars")];
        insert_string += ",";
        //type
        insert_string += add_quotes(fields_result[fields.indexOf("type")]);
        insert_string += ",";
        //open
        insert_string += add_quotes(fields_result[fields.indexOf("open")]);
        insert_string += ",";

        for (String s : hour_fields_result) {
            insert_string += add_quotes(s);
            insert_string += ",";
        }

        insert_string = remove_last_character(insert_string);
        insert_string += ")";

        System.out.println(insert_string);
        System.out.println(jdbc_handler.makeUpdateQuery(insert_string));

        insert_to_business_attributes(fields_result[fields.indexOf("business_id")], attributes);
        insert_to_business_categories(fields_result[fields.indexOf("business_id")], categories);


        return insert_string;
    }


    private void insert_to_business_attributes(String business_id, JSONObject entry) {
        for (Object key : entry.keySet()) {
            String insert_string = "INSERT INTO Attributes VALUES(";
            insert_string += add_quotes(business_id);
            insert_string += ",";
            insert_string += add_quotes(key.toString());
            insert_string += ",";
            insert_string += add_quotes(entry.get(key).toString());
            insert_string += ")";

            System.out.println(insert_string);
            System.out.println(jdbc_handler.makeUpdateQuery(insert_string));
        }

    }

    private void insert_to_business_categories(String business_id, JSONArray entry) {
        String table_name = "";
        for (Object s : entry) {
            if(isBusinessCategory(s.toString())) {
                table_name = "MainCategories";
            }else{
                table_name = "SubCategories";
            }
            String insert_string = "INSERT INTO "+table_name+" VALUES(";
            insert_string += add_quotes(business_id);
            insert_string += ",";
            insert_string += add_quotes(s.toString());
            insert_string += ")";


            System.out.println(insert_string);
            System.out.println(jdbc_handler.makeUpdateQuery(insert_string));
        }

    }


    /**
     * This will covert the json object into an equivalent Database Query Insert String
     *
     * @param entry : "line" of the json input file
     * @return Database Query INSERT String
     */
    private String insert_to_review(JSONObject entry) {

        List<String> fields = Arrays.asList("review_id", "date", "stars", "text", "type", "user_id", "business_id");
        List<String> vote_fields = Arrays.asList("cool", "useful", "funny");

        JSONObject votes = (JSONObject) entry.get("votes");

        String[] fields_result = new String[fields.size()];
        String[] votes_fields_result = new String[vote_fields.size()];

        for (int i = 0; i < fields.size(); i++) {
//            System.out.println(fields.get(i) + entry.get(fields.get(i)));
            fields_result[i] = entry.get(fields.get(i)).toString();
        }

        for (int i = 0; i < vote_fields.size(); i++) {
//            System.out.println(vote_fields.get(i) + votes.get(vote_fields.get(i)));
            votes_fields_result[i] = votes.get(vote_fields.get(i)).toString();
        }


        String insert_string = "INSERT INTO Review VALUES(";
        //review_id
        insert_string += add_quotes(fields_result[fields.indexOf("review_id")]);
        insert_string += ",";
        //date_string
        insert_string += add_quotes(fields_result[fields.indexOf("date")]);
        insert_string += ",";
        //v_cool
        insert_string += votes_fields_result[vote_fields.indexOf("cool")];
        insert_string += ",";
        //v_useful
        insert_string += votes_fields_result[vote_fields.indexOf("useful")];
        insert_string += ",";
        //v_funny
        insert_string += votes_fields_result[vote_fields.indexOf("funny")];
        insert_string += ",";
        //stars
        insert_string += fields_result[fields.indexOf("stars")];
        insert_string += ",";
        //text
        insert_string += add_quotes(fields_result[fields.indexOf("text")]);
        insert_string += ",";
        //type
        insert_string += add_quotes(fields_result[fields.indexOf("type")]);
        insert_string += ",";
        //user_id
        insert_string += add_quotes(fields_result[fields.indexOf("user_id")]);
        insert_string += ",";
        //business_id
        insert_string += add_quotes(fields_result[fields.indexOf("business_id")]);
        insert_string += ")";

        System.out.println(insert_string);
        System.out.println(jdbc_handler.makeUpdateQuery(insert_string));
        return insert_string;
    }


    /**
     * This will covert the json object into an equivalent Database Query Insert String
     *
     * @param entry : "line" of the json input file
     * @return Database Query INSERT String
     */
    private String insert_to_checkin(JSONObject entry) {

        List<String> fields = Arrays.asList("business_id", "type", "checkin_info");
        String[] fields_result = new String[fields.size()];

        //TODO CHECKIN INFO?????

        for (int i = 0; i < fields.size(); i++) {
            fields_result[i] = entry.get(fields.get(i)).toString();
        }

        String insert_string = "INSERT INTO CheckIn VALUES(";
        //user_id
        insert_string += add_quotes(fields_result[fields.indexOf("business_id")]);
        insert_string += ",";
        //name
        insert_string += add_quotes(fields_result[fields.indexOf("type")]);
        insert_string += ",";
        //yelping_since
        insert_string += add_quotes(fields_result[fields.indexOf("checkin_info")]);
        insert_string += ")";

        System.out.println(insert_string);
        return insert_string;
    }
}
