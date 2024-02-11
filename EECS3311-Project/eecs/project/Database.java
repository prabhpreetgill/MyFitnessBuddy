package eecs.project;


import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.FileReader;
import java.sql.*;

public class Database {
    public static Connection connect;

    static {
        try {
            connect = DriverManager
                    .getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Database() {
        try {
//            connect = DriverManager
//                    .getConnection("jdbc:sqlite::memory:");

//            createTables();
//            loadCsvs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createTables() throws SQLException {
        Statement statement = connect.createStatement();

        statement.execute("create table if not exists yield_name(\n" +
                "YieldID int, \n" +
                "YieldName varchar(200),\n" +
                "YieldNameF varchar(200) \n" +
                ")\n");

        statement.execute("create table if not exists refuse_name(\n" +
                "RefuseID int,\n" +
                "RefuseDescription varchar(200),\n" +
                "RefuseDescriptionF varchar(200)\n" +
                ");\n");

        statement.execute("create table if not exists measure_name(\n" +
                "MeasureUD int,\n" +
                "MeasureName varchar(200),\n" +
                "MeasureNameF varchar(200)\n" +
                ");");

        statement.execute("create table if not exists yield_amount(\n" +
                "FoodID int,\n" +
                "YieldID int,\n" +
                "YieldAmount int,\n" +
                "YieldDateOfEntry date\n" +
                ");\n" +
                "\n");
        statement.execute(
                "create table if not exists refuse_amount(\n" +
                        "FoodID int,\n" +
                        "RefuseID int,\n" +
                        "RefuseAmount int,\n" +
                        "RefuseDateOfEntry date\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists conversion_fac(\n" +
                        "FoodID int,\n" +
                        "MeasureID int,\n" +
                        "ConversionFactionValue int,\n" +
                        "ConvFactorDateOfEntry date\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists nutrient_name(\n" +
                        "NutrientID int,\n" +
                        "NutrientCode int,\n" +
                        "NutrientSymbol varchar(200),\n" +
                        "NutrientUnit varchar(8),\n" +
                        "NutrientName varchar(200),\n" +
                        "NutrientNameF varchar(200),\n" +
                        "Tagname varchar(200),\n" +
                        "NutrientDecimals int\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists food_name(\n" +
                        "FoodID int,\n" +
                        "FoodCode int,\n" +
                        "FoodGroupID int,\n" +
                        "FoodSourceID int,\n" +
                        "FoodDescription varchar(255),\n" +
                        "FoodDescriptionF varchar(255),\n" +
                        "FoodDateOfEntry date,\n" +
                        "FoodDateOfPublication date,\n" +
                        "CountryCode int,\n" +
                        "ScientificName varchar(200)\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists nutrient_amount(\n" +
                        "FoodID int,\n" +
                        "NutrientNameID int,\n" +
                        "Nutrient int,\n" +
                        "StandardError int,\n" +
                        "NumberOfObservation int,\n" +
                        "NutrientSourceID int,\n" +
                        "NutrientDateEntry date\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists food_group(\n" +
                        "FoodGroupID int,\n" +
                        "FoodGroupCode int,\n" +
                        "FoodGroupName varchar(200),\n" +
                        "FoodGroupNameF varchar(200)\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists food_source(\n" +
                        "FoodSourceID int,\n" +
                        "FoodSourceCode int,\n" +
                        "FoodSourceDescription varchar(200),\n" +
                        "FoodSourceDescriptionF varchar(200)\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists nutrient_source(\n" +
                        "NutrientSourceID int,\n" +
                        "NutrientSourceCode int,\n" +
                        "NutrientSourceDescription varchar(200),\n" +
                        "NutrientSourceDescriptionF varchar(200)\n" +
                        ");\n" +
                        "\n");
        statement.execute(
                "create table if not exists profiles(\n" +
                        "Sex varchar(6),\n" +
                        "DateOfBirth date,\n" +
                        "Height int,\n" +
                        "Weight int,\n" +
                        "UnitPreference varchar(8)\n" +
                        ");");

        statement.execute("insert into profiles(Sex, DateOfBirth, Height, Weight, UnitPreference)" +
                "VALUES('Male', '1995-09-02', 180, 180, 'metric');");

        statement.close();
    }

    void loadCsvs() throws Exception {
        loadConversionFac("CONVERSION FACTOR.csv");
        loadFoodGroup("FOOD GROUP.csv");
        loadFoodName("FOOD NAME.csv");
        loadFoodSource("FOOD SOURCE.csv");
        loadMeasureName("MEASURE NAME.csv");
        loadNutrientAmount("NUTRIENT AMOUNT.csv");
        loadNutrientName("NUTRIENT NAME.csv");
        loadNutrientSource("NUTRIENT SOURCE.csv");
        loadRefuseAmount("REFUSE AMOUNT.csv");
        loadRefuseName("REFUSE NAME.csv");
        loadYieldAmount("YIELD AMOUNT.csv");
        loadYieldName("YIELD NAME.csv");
    }

    void loadYieldName(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO yield_name VALUES (?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            if (line[0] == null) {
                continue;
            }
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setString(2, line[1].trim());
            preparedStatement.setString(3, line[2].trim());

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadRefuseName(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO refuse_name VALUES (?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            if (line[0] == null) {
                continue;
            }
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setString(2, line[1].trim());
            preparedStatement.setString(3, line[2].trim());

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadMeasureName(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO measure_name VALUES (?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setString(2, line[1].trim());
            preparedStatement.setString(3, line[2].trim());

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadYieldAmount(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO yield_amount VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setInt(3, Integer.parseInt(line[2].trim()));
            preparedStatement.setDate(4, Date.valueOf(line[3].trim()));

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadRefuseAmount(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO refuse_amount VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setInt(3, Integer.parseInt(line[2].trim()));
            preparedStatement.setDate(4, Date.valueOf(line[3].trim()));

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadConversionFac(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO conversion_fac VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setDouble(3, Double.parseDouble(line[2].trim()));
            preparedStatement.setDate(4, Date.valueOf(line[3].trim()));

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadNutrientName(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO nutrient_name VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setString(3, line[2].trim());
            preparedStatement.setString(4, line[3].trim());
            preparedStatement.setString(5, line[4].trim());
            preparedStatement.setString(6, line[5].trim());
            preparedStatement.setString(7, line[6] != null ? line[6].trim() : null);
            preparedStatement.setInt(8, Integer.parseInt(line[7].trim()));

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadFoodName(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO food_name VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            try {
                preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
                preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
                preparedStatement.setInt(3, Integer.parseInt(line[2].trim()));
                preparedStatement.setInt(4, Integer.parseInt(line[3].trim()));
                preparedStatement.setString(5, line[4].trim());
                preparedStatement.setString(6, line[5].trim());
                preparedStatement.setDate(7, line[6] == null ? null : Date.valueOf(line[6]));
                preparedStatement.setDate(8, line[7] == null ? null : Date.valueOf(line[7]));
                preparedStatement.setInt(9, line[8] == null ? 0 : Integer.parseInt(line[8]));
                preparedStatement.setString(10, line[9]);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                System.err.println(line[6]);
                e.printStackTrace();
            }

        }
        csv.close();
    }

    void loadNutrientAmount(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO nutrient_amount VALUES (?, ?, ?, ?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            if (line[2] != null && !line[2].trim().isEmpty())
                preparedStatement.setDouble(3, Double.parseDouble(line[2].trim()));
            else
                preparedStatement.setNull(3, java.sql.Types.DOUBLE);

// Check for null or empty string before parsing as Integer
            if (line[3] != null && !line[3].trim().isEmpty())
                preparedStatement.setDouble(4, Double.parseDouble(line[3].trim()));
            else
                preparedStatement.setNull(4, java.sql.Types.INTEGER);

// Check for null or empty string before parsing as Integer
            if (line[4] != null && !line[4].trim().isEmpty())
                preparedStatement.setInt(5, Integer.parseInt(line[4].trim()));
            else
                preparedStatement.setNull(5, java.sql.Types.INTEGER);

// Check for null or empty string before parsing as Integer
            if (line[5] != null && !line[5].trim().isEmpty())
                preparedStatement.setInt(6, Integer.parseInt(line[5].trim()));
            else
                preparedStatement.setNull(6, java.sql.Types.INTEGER);// Check for null or empty string before parsing as Date

            if (line[6] != null && !line[6].trim().isEmpty())
                preparedStatement.setDate(7, Date.valueOf(line[6].trim()));
            else
                preparedStatement.setNull(7, java.sql.Types.DATE);
            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadFoodGroup(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO food_group VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            try {
                preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
                preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
                preparedStatement.setString(3, line[2].trim());
                preparedStatement.setString(4, line[3].trim());

                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        csv.close();
    }

    void loadFoodSource(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO food_source VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setString(3, line[2].trim());
            preparedStatement.setString(4, line[3].trim());

            preparedStatement.executeUpdate();
        }
        csv.close();
    }

    void loadNutrientSource(String filename) throws Exception {
        CSVReader csv = new CSVReaderBuilder(new FileReader("CSV FILES/" + filename)).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).withSkipLines(1).build();
        PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO nutrient_source VALUES (?, ?, ?, ?)");
        String[] line;

        while ((line = csv.readNext()) != null) {
            preparedStatement.setInt(1, Integer.parseInt(line[0].trim()));
            preparedStatement.setInt(2, Integer.parseInt(line[1].trim()));
            preparedStatement.setString(3, line[2].trim());
            preparedStatement.setString(4, line[3] == null ? null : line[3].trim());

            preparedStatement.executeUpdate();
        }
        csv.close();
    }


    // Similarly, complete the remaining methods for other tables following the same pattern.
    public static void main(String[] args) {
        new Database();
    }

}