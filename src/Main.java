public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View(model);
        new Controller(model, view);

        view.pack();
        view.setTitle("Chicken & Nuggets");
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }
}
