import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    static Scanner scanner;
    static EntityDao entityDao;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        entityDao = new EntityDao();

        String command;
        do {
            System.out.println("Wpisz komendę:");
            System.out.println("1 -> wypisz faktury");
            System.out.println("2 -> dodaj fakturę");
            System.out.println("3 -> wypisz fakturę po id");
            System.out.println("4 -> dodaj produkt do faktury");
            System.out.println("5 -> opłać fakturę");
            System.out.println("6 -> wypisz sumę z faktury");
            System.out.println("7 -> wypisz produkty z faktury");
            System.out.println("8 -> wypisz wszystkie produkty");
            System.out.println("9 -> wypisz rachunki nieopłacone");
            System.out.println("10 -> wypisz rachunki z ostatniego dnia");
            System.out.println("11 -> wypisz sumę z rachunków z obecnego dnia");
            System.out.println("0 -> koniec");

            command = scanner.nextLine();

            if (command.equalsIgnoreCase("1")) { //wypisz faktury
                entityDao.getAll(Invoice.class).forEach(System.out::println);

            } else if  (command.equalsIgnoreCase("2")) { //dodaj fakturę
                Invoice invoice = new Invoice();
                System.out.println("wpisz nazwę klienta");
                invoice.setClientName(scanner.nextLine());
                invoice.setDateTimeCreated(LocalDateTime.now());
                System.out.println("dodano fakturę? " + entityDao.insertOrUpdate(invoice));

            } else if (command.equalsIgnoreCase("3")) { //wypisz fakturę po id
                Invoice invoice = getInvoiceFromDbById();
                System.out.println(invoice);

            } else if (command.equalsIgnoreCase("4")) { //dodaj produkt do faktury
                Invoice invoice = getInvoiceFromDbById();
                if (invoice.isPaid()){
                    System.out.println("Faktura jest już opłacona, nie można dodać produktu");
                    continue;
                }
                System.out.println("Dodajesz produkty do faktury: " + invoice);

                String again;
                do {
                    Product product = createProduct(invoice);
                    entityDao.insertOrUpdate(product);
                    System.out.println("Chcesz dodać kolejny produkt do tej faktury? (wpisz tak)");
                    again = scanner.nextLine();
                } while (again.equalsIgnoreCase("tak"));

            } else if (command.equalsIgnoreCase("5")) { //opłać fakturę
                Invoice invoice = getInvoiceFromDbById();
                if (!invoice.isPaid()) {
                    invoice.setPaid(true);
                    invoice.setDateTimePaid(LocalDateTime.now());
//                    System.out.println(invoice);
                    entityDao.insertOrUpdate(invoice);
                } else {
                    System.out.println("Faktura została opłacona");
                }

            } else if (command.equalsIgnoreCase("6")) { //wypisz sumę z faktury
                Invoice invoice = getInvoiceFromDbById();
                System.out.println("suma z faktury o id = " + invoice.getId() + " wynosi: " + invoice.getSum());

            } else if (command.equalsIgnoreCase("7")) { //wypsiz produkty z faktury
                Invoice invoice = getInvoiceFromDbById();
                invoice.getProducts().forEach(System.out::println);

            } else if (command.equalsIgnoreCase("8")) { //wypisz wszystkie produkty
                entityDao.getAll(Product.class).forEach(System.out::println);

            } else if (command.equalsIgnoreCase("9")) { //wypisz rachunki nieopłacone
                //TODO
            } else if (command.equalsIgnoreCase("10" )){ // wypisz rachunki z ostatniego dnia
                //TODO
            } else if (command.equalsIgnoreCase("11" )){ // wypisz sumę z rachunków z obecnego dnia
                //TODO
            }

        } while (!command.equalsIgnoreCase("0"));







    }

    private static Product createProduct(Invoice invoice) {
        System.out.println("Wpisz nazwę produktu");
        String name = scanner.nextLine();
        System.out.println("Wpisz cenę netto");
        Double price = getDoubleFromUser();
        System.out.println("Wpisz podatek");
        Double tax = getDoubleFromUser();
        System.out.println("podaj ilość");
        int amount = getIntFromUser();
        return new Product(name, amount, price, tax, invoice);
    }


    private static Invoice getInvoiceFromDbById() {
        Optional<Invoice> optional;
        do {
            System.out.println("wpisz id");
            long id = getLongFromUser();
            optional = entityDao.getEntityById(Invoice.class, id);
            if (!optional.isPresent()) {
                System.out.println("nie znaleziono instancji");
            }
        } while (!optional.isPresent());
        return optional.get();
    }

    private static long getLongFromUser() {
        long value = Long.parseLong(scanner.nextLine());
        return value;
    }

    private static double getDoubleFromUser() {
        double value = Double.parseDouble(scanner.nextLine());
        return value;
    }

    private static int getIntFromUser() {
        int value = Integer.parseInt(scanner.nextLine());
        return value;
    }
}
