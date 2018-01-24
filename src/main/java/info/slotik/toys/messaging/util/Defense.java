package info.slotik.toys.messaging.util;

public class Defense
{
    public static void notNull(String message, Object object) {
        if(object == null) {
            throw new DefenseException(message);
        }
    }

}
