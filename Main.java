public class Main {
    public static void main(String[] args) {
        CoffeeMachineAI machine = new CoffeeMachineAI();

        while (true) {
            machine.showMainMenu();
            int choice = CoffeeMachineAI.getVoiceInput(1, 5);

            switch (choice) {
                case 1 -> {
                    System.out.println("👉 매장에서 마시기 선택됨.\n");
                    machine.orderCoffee();
                }
                case 2 -> {
                    System.out.println("👉 포장하기 선택됨.\n");
                    machine.orderCoffee();
                }
                case 3 -> machine.showPaymentGuide();
                case 4 -> machine.callStaff();
                case 5 -> {
                    System.out.println("🛑 프로그램을 종료합니다. 감사합니다.");
                    System.exit(0);
                }
            }
        }
    }
}
