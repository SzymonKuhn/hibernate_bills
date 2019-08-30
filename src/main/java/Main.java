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

            command = scanner.nextLine();

            if (command.equalsIgnoreCase("1")) {
                entityDao.getAll(Invoice.class).forEach(System.out::println);
            } else if  (command.equalsIgnoreCase("2")) {
                Invoice invoice = new Invoice();
                System.out.println("wpisz nazwę klienta");
                invoice.setClientName(scanner.nextLine());
                invoice.setDateTimeCreated(LocalDateTime.now());
                System.out.println("dodano fakturę? " + entityDao.insertOrUpdate(invoice));
            } else if (command.equalsIgnoreCase("3")) {
                Invoice invoice = getInvoiceFromDbById();
                System.out.println(invoice);
            } else if (command.equalsIgnoreCase("4")) {
                Invoice invoice = getInvoiceFromDbById();
                if (invoice.isPaid()){
                    System.out.println("Faktura jest opłacona, nie można dodać produktu");
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

            } else if (command.equalsIgnoreCase("5")) {
                Invoice invoice = getInvoiceFromDbById();
                if (!invoice.isPaid()) {
                    invoice.setPaid(true);
                    invoice.setDateTimePaid(LocalDateTime.now());
//                    System.out.println(invoice);
                    entityDao.insertOrUpdate(invoice);
                } else {
                    System.out.println("Faktura jest opłacona");
                }
            }

        } while (!command.equalsIgnoreCase("quit"));







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
