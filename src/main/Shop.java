package main;

import model.Product;
import model.Sale;
import model.Amount;

import java.util.ArrayList;
import java.util.Scanner;

public class Shop
{
    private Amount cash = new Amount(100.00); 
    private ArrayList<Product> inventory = new ArrayList<>();
    private ArrayList<Sale> sales = new ArrayList<>();
    final static double TAX_RATE = 1.04;

    public static void main(String[] args)
    {
        Shop shop = new Shop();
        shop.loadInventory();

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        boolean exit = false;

        do
        {
            System.out.println("===========================");
            System.out.println("Menu principal miTienda.com");
            System.out.println("===========================");
            System.out.println("1) Contar caja");
            System.out.println("2) Añadir producto");
            System.out.println("3) Añadir stock");
            System.out.println("4) Marcar producto caducado");
            System.out.println("5) Ver inventario");
            System.out.println("6) Venta");
            System.out.println("7) Ver ventas");
            System.out.println("8) Dinero total de todas las ventas");
            System.out.println("10) Salir programa");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion)
            {
                case 1 -> shop.showCash();
                case 2 -> shop.addProduct();
                case 3 -> shop.addStock();
                case 4 -> shop.setExpired();
                case 5 -> shop.showInventory();
                case 6 -> shop.sale();
                case 7 -> shop.showSales();
                case 8 -> shop.totalAmount();
                case 10 -> exit = true;
                default -> System.out.println("Opción no válida.");
            }
        }
        while (!exit);
    }

    public void loadInventory()
    {
        inventory.add(new Product("Manzana", 10.00, true, 10));
        inventory.add(new Product("Pera", 20.00, true, 20));
        inventory.add(new Product("Hamburguesa", 30.00, true, 30));
        inventory.add(new Product("Fresa", 5.00, true, 20));
    }

    private void showCash()
    {
        System.out.println("\nDinero actual: " + cash);
        System.out.println();
    }

    public void addProduct()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("\nNombre: ");
        String name = scanner.nextLine();

        for (Product product : inventory)
        {
            if (product.getName().equalsIgnoreCase(name))
            {
                System.out.println("El producto que intentas añadir ya existe.");
                System.out.println();
                return;
            }
        }

        System.out.print("Precio mayorista: ");
        double wholesalerPrice = scanner.nextDouble();
        System.out.print("Stock: ");
        int stock = scanner.nextInt();

        inventory.add(new Product(name, wholesalerPrice, true, stock));
        System.out.println("Producto añadido con éxito.");
        System.out.println();
    }

    public void addStock()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("\nSeleccione un nombre de producto: ");
        String name = scanner.next();
        Product product = findProduct(name);

        if (product != null)
        {
            System.out.print("Seleccione la cantidad a añadir: ");
            int stock = scanner.nextInt();
            product.setStock(product.getStock() + stock);
            System.out.println("El stock del producto " + name + " ha sido actualizado a " + product.getStock() + ".");
            System.out.println();
        }
        else
        {
            System.out.println("No se ha encontrado el producto con nombre " + name + ".");
            System.out.println();
        }
    }

    private void setExpired()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("\nSeleccione un nombre de producto: ");
        String name = scanner.next();

        Product product = findProduct(name);

        if (product != null)
        {
            product.expire();
            System.out.println("El precio del producto " + name + " ha sido actualizado a " + product.getPublicPrice());
            System.out.println();
        }
        else
        {
            System.out.println("No se ha encontrado el producto.");
            System.out.println();
        }
    }

    public void showInventory()
    {
        System.out.println("\nContenido actual de la tienda:");
        System.out.println();
        
        for (Product product : inventory)
        {
            System.out.println("[" + product.getName() + "]");
            System.out.println("- Precio mayorista: " + product.getWholesalerPrice());
            System.out.println("- Disponible: " + product.isAvailable());
            System.out.println("- Stock: " + product.getStock());
            System.out.println();
        }
    }

    public void sale()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Realizar venta, escribir nombre cliente:");
        String client = sc.nextLine();

        double totalAmount = 0.0;
        ArrayList<Product> purchasedProducts = new ArrayList<>();
        String name = "";
        while (!name.equals("0"))
        {
            System.out.println("Introduce el nombre del producto, escribir 0 para terminar:");
            name = sc.nextLine();

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
                System.out.println("Producto añadido con éxito.");
            }
            else
            {
                System.out.println("Producto no encontrado o sin stock.");
            }
        }

        totalAmount *= TAX_RATE;
        cash.setValue(cash.getValue() + totalAmount);
        sales.add(new Sale(client, purchasedProducts.toArray(new Product[0]), totalAmount));
        System.out.println("Venta realizada con éxito, total: " + new Amount(totalAmount));
    }

    public void showSales()
    {
        if (sales.isEmpty())
        {
            System.out.println("\nNo hay ventas registradas.");
            System.out.println();
            
            return;
        }
        
        System.out.println("\nLista de ventas:");
        System.out.println();
        
        for (Sale sale : sales)
        {
            System.out.println("Nombre del cliente: " + sale.getClient());
            System.out.print("Productos comprados: ");
            for (Product product : sale.getProducts())
            {
                System.out.print(product.getName() + " ");
            }
            System.out.println("\nPrecio: " + sale.getAmount());
            System.out.println();
        }
    }

    public void totalAmount()
    {
        double totalVentas = 0;
        
        if (sales.isEmpty())
        {
            System.out.println("\nNo hay ventas registradas.");
            System.out.println();
            
            return;
        }
        
        for (Sale sale : sales)
        {
            totalVentas += sale.getAmount().getValue();
        }
        
        System.out.print("\nEl total de dinero de todas las ventas es: " + new Amount(totalVentas));
        System.out.println();
    }
    
    //Método que le damos un String y devuelve el producto con ese nombre o null;
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
}
