import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class UnixTime {

    private int value;

    public UnixTime(){
       this((int)System.currentTimeMillis());
    }

    public UnixTime(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(){
        return new Date(value).toString();
    }
}
