package fa.training.components.utils;

public class Constant {
    public static final String SECRET = "A secret string used to create JWT token";
    public static final int ONE_HOUR = 1*60*60; // In seconds

    /**
     * Error: "Order not found!"
     */
    public static final String ERROR_ORDER_NOT_FOUND = "Order not found!";

    /**
     * Error: "Order Detail not found!"
     */
    public static final  String ERROR_ORDER_DETAIL_NOT_FOUND = "Order Details not found!";

    /**
     * Regex: Order Status: require value "ordered" or "paid"
     */
    public static final String REGEX_ORDER_STATUS = "^(?:ordered )?paid$";

    /**
     * Error: Order Status must be "ordered" or "paid"
     */
    public static final String ERROR_ORDER_STATUS_INVALID = "Order Status must be ordered or paid";

}
