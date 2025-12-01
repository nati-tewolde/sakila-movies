package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            if (args.length != 2) {
                System.out.println("This application needs a Username and Password too run!");
                System.exit(1);
            }

            String username = args[0];
            String password = args[1];

            try (BasicDataSource dataSource = new BasicDataSource()) {
                dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
                dataSource.setUsername(username);
                dataSource.setPassword(password);

                System.out.print("\nPlease enter the last name of an actor: ");
                String lastNameToSearch = scanner.nextLine();

                queryActorsByName(dataSource, lastNameToSearch);

                System.out.print("\nTo find movies by actor, please enter the first name of the actor: ");
                String firstNameForMovie = scanner.nextLine();

                System.out.print("\nPlease enter the last name of the actor: ");
                String lastNameForMovie = scanner.nextLine();

                queryMoviesByActor(dataSource, firstNameForMovie, lastNameForMovie);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void queryActorsByName(BasicDataSource dataSource, String lastNameToSearch) {
        String query = """
                SELECT first_name, last_name
                FROM Actor a
                WHERE a.last_name = ?;
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, lastNameToSearch);
            try (ResultSet results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    System.out.println("\nActors with that last name: ");
                    do {
                        String firstName = results.getString("first_name");
                        String lastName = results.getString("last_name");

                        System.out.println("First Name: " + firstName);
                        System.out.println("Last Name: " + lastName);
                        System.out.println("-----------------------------------------");
                    } while (results.next());
                } else {
                    System.out.println("\nNo matches!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void queryMoviesByActor(BasicDataSource dataSource, String firstNameForMovie, String lastNameForMovie) {
        String query = """
                SELECT title
                FROM Film f
                JOIN film_actor fa ON f.film_id = fa.film_id
                JOIN actor a ON fa.actor_id = a.actor_id
                WHERE a.first_name = ? AND a.last_name = ?;
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstNameForMovie);
            preparedStatement.setString(2, lastNameForMovie);
            try (ResultSet results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    System.out.println("\nFilms with that actor: ");
                    do {
                        String movieTitle = results.getString("title");
                        System.out.println(movieTitle);
                        System.out.println("-----------------------------------------");
                    } while (results.next());
                } else {
                    System.out.println("\nNo matches!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


