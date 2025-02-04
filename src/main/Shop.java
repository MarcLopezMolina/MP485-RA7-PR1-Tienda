package main;

import java.text.DecimalFormat;
import model.Product;
import model.Sale;
import model.Amount;
import model.Employee;
import model.Client;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Shop
{
    private Amount cash = new Amount(100.00); 
    private ArrayList<Product> inventory = new ArrayList<>();
    private ArrayList<Sale> sales = new ArrayList<>();
    final static double TAX_RATE = 1.04;

    public static void main(String[] args)
    {
        initSession();
        Shop shop = new Shop();
        shop.loadInventory();

        int option = 0;
        boolean exit = false;

        do
        {
            System.out.println("===========================");
            System.out.println("Main menu myShop.com");
            System.out.println("===========================");
            System.out.println("1) Show cash");
            System.out.println("2) Add product");
            System.out.println("3) Add stock");
            System.out.println("4) Mark a product as expired");
            System.out.println("5) Show inventory");
            System.out.println("6) Sale");
            System.out.println("7) Show sales");
            System.out.println("8) Show total amount of sales");
            System.out.println("9) Delete product");
            System.out.println("10) Exit program");
            System.out.print("Select an option: ");
            option = scannerInt();

            switch (option)
            {

                case 1 -> shop.showCash();
                case 2 -> shop.addProduct();
                case 3 -> shop.addStock();
                case 4 -> shop.setExpired();
                case 5 -> shop.showInventory();
                case 6 -> shop.sale();
                case 7 -> shop.showSales();
                case 8 -> shop.totalAmount();
                case 9 -> shop.deleteProduct();
                case 10 -> exit = true;
                default -> System.out.println("Option not available");
            }
        }
        while (!exit);
    }
    
    //Void to login
    public static void initSession()
    {
        boolean login = false;
                
        do
        {   
            System.out.print("Introduce the number of employee: ");
            int employeeId = scannerInt();
            System.out.print("Introduce password: ");
            String password = scannerString();
            
            Employee employee = new Employee ("test", employeeId, password);
            
            login = employee.login(employeeId, password);
        }
        while(login == false);
    }
    
    public void loadInventory()
    {
        inventory.add(new Product("Apple", 10.00, true, 10));
        inventory.add(new Product("Pear", 20.00, true, 20));
        inventory.add(new Product("Hamburguer", 30.00, true, 30));
        inventory.add(new Product("Strawberry", 5.00, true, 20));
    }

    private void showCash()
    {
        System.out.println("\nTotal cash: " + cash);
        System.out.println();
    }

    public void addProduct()
    {        
        System.out.print("\nName: ");
        String name = scannerString();

        for (Product product : inventory)
        {
            if (product.getName().equalsIgnoreCase(name))
            {
                System.out.println("That product already exists.");
                System.out.println();
                return;
            }
        }

        System.out.print("Wholesaler price: ");
        double wholesalerPrice = scannerDouble();
        System.out.print("Stock: ");
        int stock = scannerInt();

        inventory.add(new Product(name, wholesalerPrice, true, stock));
        System.out.println("Product added successfully.");
        System.out.println();
    }

    public void addStock()
    {   
        System.out.print("\nProduct name: ");
        String name = scannerString();
        Product product = findProduct(name);

        if (product != null)
        {
            System.out.print("How manny stock do you want to add? ");
            int stock = scannerInt();
            product.setStock(product.getStock() + stock);
            System.out.println("The stock of " + name + " has been updated to " + product.getStock() + ".");
            System.out.println();
        }
        else
        {
            System.out.println("There is no product with the name " + name + ".");
            System.out.println();
        }
    }

    private void setExpired()
    {
        System.out.print("\nProduct name: ");
        String name = scannerString();

        Product product = findProduct(name);

        if (product != null)
        {
            product.expire();
            System.out.println("The price of " + name + " has been updated to " + product.getPublicPrice());
            System.out.println();
        }
        else
        {
            System.out.println("The product wasn't found.");
            System.out.println();
        }
    }

    public void showInventory()
    {
        System.out.println("\nInventory of the shop:");
        System.out.println();
        
        for (Product product : inventory)
        {
            System.out.println("[" + product.getName() + "]");
            System.out.println("- Wholesaler price: " + product.getWholesalerPrice());
            System.out.println("- Available: " + product.isAvailable());
            System.out.println("- Stock: " + product.getStock());
            System.out.println();
        }
    }

    public void sale()
    {   
        DecimalFormat df = new DecimalFormat("#.00");
        Amount bill = new Amount (0);
        double totalAmount = 0.0;
        ArrayList<Product> purchasedProducts = new ArrayList<>();
        int memberId = 0;
        
        System.out.print("\nType the name of the customer: ");
        String name = scannerString();
        Client client = new Client (name, memberId + 1, bill);
        
        while (!name.equals("0"))
        {
            System.out.print("Type the name of the product, press 0 to end: ");
            name = scannerString();

            if (name.equals("0"))
            {
                break;
            }
            
            Product product = findProduct(name);

            if (product != null && product.isAvailable() && product.getStock() > 0)
            {
                totalAmount += product.getPublicPrice().getValue();
                product.setStock(product.getStock() - 1);
                if (product.getStock() == 0)
                {
                    product.setAvailable(false);
                }
                purchasedProducts.add(product);
                System.out.println("Product added successfully.");
            }
            else
            {
                System.out.println("Product out of stock or not found.");
            }
        }
        
        totalAmount *= TAX_RATE;
        
        cash.setValue(cash.getValue() + totalAmount);
        bill.setValue(totalAmount);
        sales.add(new Sale(client, purchasedProducts.toArray(new Product[0]), bill.getValue()));
        System.out.println("Purchase done successfully, total amount: " + df.format(bill.getValue()));
        
        boolean enoughMoney = client.pay(bill);
        if (enoughMoney == false)
        {
            System.out.println("The client needs to pay: " + df.format((50 - bill.getValue())));
        }
        
        System.out.println();
    }

    public void showSales()
    {
        if (sales.isEmpty())
        {
            System.out.println("\nThere are no sales.");
            System.out.println();
            
            return;
        }
        
        System.out.println("\nList of sales:");
        System.out.println();
        
        for (Sale sale : sales)
        {
            System.out.println("Name of the customer: " + sale.getClient());
            System.out.print("Products purchased: ");
            for (Product product : sale.getProducts())
            {
                System.out.print(product.getName() + ", ");
            }
            System.out.println("\nPrice: " + sale.getAmount());
            System.out.println();
        }
    }

    //Void to know the total amount of sales, client names, their products and the bill of the purchase.
    public void totalAmount()
    {
        double totalVentas = 0;
        
        if (sales.isEmpty())
        {
            System.out.println("\nThere are no sales.");
            System.out.println();
            
            return;
        }
        
        for (Sale sale : sales)
        {
            totalVentas += sale.getAmount().getValue();
        }
        
        System.out.print("\nTotal amount of earnings from sales: " + new Amount(totalVentas));
        System.out.println();
    }
    
    //Method that search for the product and returns the product or null
    public Product findProduct(String name)
    {
        for (Product product : inventory)
        {
            if (product.getName().equals(name))
            {
                return product;
            }
        }
        return null;
    }
    
    public void deleteProduct()
    {
        System.out.print("\nProduct name: ");
        String name = scannerString();
        boolean encontrado = false;
        
        for (Product product : inventory)
        {
            if (product.getName().equalsIgnoreCase(name))
            {
                encontrado = true;
                inventory.remove(product);
                System.out.println("The product has been removed successfully.");
                System.out.println();
                return;
            }
        }
        
        if  (encontrado == false)
        {
            System.out.println("The product wasn't found.");
            System.out.println();
        }
    }
    
    //Method to use the scanner as int type to prevent saturating the buffer.
    public static int scannerInt()
    {
        Scanner sc = new Scanner(System.in);
        while (true)
        {  
            try
            {
                return sc.nextInt();  
            }
            catch (InputMismatchException e)
            {
                System.out.println("You can only type numbers. ");
                sc.nextLine();
            }
        }
    }
    
    //Method to use the scanner as String type to prevent saturating the buffer.
    public static String scannerString()
    {
        Scanner sc = new Scanner(System.in);
        while (true)
        {  
            try
            {
                return sc.nextLine();  
            }
            catch (InputMismatchException e)
            {
                System.out.println("You can only type letters and numbers. ");
                sc.nextInt();
            }
        }
    }
    
    //Method to use the scanner as double type to prevent saturating the buffer.
    public static double scannerDouble()
    {
        Scanner sc = new Scanner(System.in);
        while (true)
        {  
            try
            {
                return sc.nextDouble();  
            }
            catch (InputMismatchException e)
            {
                System.out.println("You can only type decimal numbers. ");
                sc.nextLine();
            }
        }
    }
}
