import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static ProductDAO dao = new ProductDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n********************PRODUCT MANAGEMENT****************");
            System.out.println("1. Danh sách sản phẩm");
            System.out.println("2. Thêm mới sản phẩm");
            System.out.println("3. Cập nhật sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo tên");
            System.out.println("6. Sắp xếp sản phẩm theo giá tăng dần");
            System.out.println("7. Thống kê số lượng sản phẩm theo danh mục");
            System.out.println("8. Thoát");
            System.out.print("Chọn chức năng: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    showList(dao.getAllProducts());
                    break;
                case 2:
                    addNewProduct();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    searchProduct();
                    break;
                case 6:
                    showList(dao.sortProductsByPrice());
                    break;
                case 7:
                    dao.statByCatalog();
                    break;
                case 8:
                    System.out.println("Kết thúc chương trình.");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private static void showList(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("Danh sách trống.");
        } else {
            System.out.printf("%-5s | %-20s | %-10s | %-20s | %-12s | %-15s | %s\n",
                    "ID", "Name", "Price", "Title", "Created", "Catalog", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------------");
            for (Product p : list) {
                System.out.println(p);
            }
        }
    }

    private static void addNewProduct() {
        try {
            System.out.print("Nhập tên sản phẩm: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) throw new Exception("Tên không được để trống");

            System.out.print("Nhập giá sản phẩm: ");
            float price = Float.parseFloat(scanner.nextLine());
            if (price <= 0) throw new Exception("Giá phải lớn hơn 0");

            System.out.print("Nhập tiêu đề: ");
            String title = scanner.nextLine();
            if (title.trim().isEmpty()) throw new Exception("Tiêu đề không được để trống");

            System.out.print("Nhập danh mục: ");
            String catalog = scanner.nextLine();
            if (catalog.trim().isEmpty()) throw new Exception("Danh mục không được để trống");

            Date created = Date.valueOf(LocalDate.now());
            int status = 1; 

            Product p = new Product(name, price, title, created, catalog, status);
            if (dao.addProduct(p)) {
                System.out.println("Thêm thành công!");
            } else {
                System.out.println("Thêm thất bại (Có thể tên đã tồn tại).");
            }
        } catch (Exception e) {
            System.out.println("Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        try {
            System.out.print("Nhập ID sản phẩm cần sửa: ");
            int id = Integer.parseInt(scanner.nextLine());
            Product existing = dao.getProductById(id);
            if (existing == null) {
                System.out.println("Sản phẩm không tồn tại!");
                return;
            }

            System.out.print("Nhập tên mới (Enter giữ nguyên): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) existing.setName(name);

            System.out.print("Nhập giá mới (Enter giữ nguyên): ");
            String priceStr = scanner.nextLine();
            if (!priceStr.trim().isEmpty()) {
                float price = Float.parseFloat(priceStr);
                if (price > 0) existing.setPrice(price);
            }

            System.out.print("Nhập tiêu đề mới (Enter giữ nguyên): ");
            String title = scanner.nextLine();
            if (!title.trim().isEmpty()) existing.setTitle(title);

            System.out.print("Nhập danh mục mới (Enter giữ nguyên): ");
            String catalog = scanner.nextLine();
            if (!catalog.trim().isEmpty()) existing.setCatalog(catalog);
            
            System.out.print("Nhập trạng thái (0: Ẩn, 1: Hiện, Enter giữ nguyên): ");
            String statusStr = scanner.nextLine();
            if(!statusStr.trim().isEmpty()){
                int status = Integer.parseInt(statusStr);
                if(status == 0 || status == 1) existing.setStatus(status);
            }

            if (dao.updateProduct(existing)) {
                System.out.println("Cập nhật thành công!");
            } else {
                System.out.println("Cập nhật thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        try {
            System.out.print("Nhập ID sản phẩm cần xóa: ");
            int id = Integer.parseInt(scanner.nextLine());
            if (dao.deleteProduct(id)) {
                System.out.println("Xóa thành công!");
            } else {
                System.out.println("Xóa thất bại (ID không tồn tại).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID phải là số.");
        }
    }

    private static void searchProduct() {
        System.out.print("Nhập tên sản phẩm cần tìm: ");
        String keyword = scanner.nextLine();
        showList(dao.searchByName(keyword));
    }
}
