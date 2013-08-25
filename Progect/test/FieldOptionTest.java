import org.junit.Assert;
import org.junit.Test;

import helper.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class FieldOptionTest {

    @Test
    public void testInstance() {
        FieldOption[] fo = new FieldOption[4];
        fo[0] = FieldOption.getInstance("0 weheh eyww");
        fo[1] = FieldOption.getInstance("1 host localhost");
        fo[2] = FieldOption.getInstance("1 something localhost");
        fo[3] = FieldOption.getInstance("3 some some");
        Assert.assertNull(fo[0]);
        Assert.assertNotNull(fo[1]);
        Assert.assertNotNull(fo[2]);
        Assert.assertNull(fo[3]);
        Assert.assertEquals(fo[1], fo[2]);
    }

    @Test
    public void testGetKind(){
        FieldOption[] fo = new FieldOption[3];
        fo[0] = FieldOption.getInstance("0 weheh eyww");
        fo[1] = FieldOption.getInstance("1 host localhost");
        fo[2] = FieldOption.getInstance("1 something localhost");
        List<FieldOption> list = new ArrayList<FieldOption>();
        for(FieldOption f : fo)
            list.add(f);
        Assert.assertEquals(fo[1], FieldOption.getFirstFieldOption(list, FieldOptionConstats.OPTION_HOST));
        Assert.assertNotSame(fo[2], FieldOption.getFirstFieldOption(list, FieldOptionConstats.OPTION_HOST));
        Assert.assertNotNull(FieldOption.getFirstFieldOption(list, FieldOptionConstats.OPTION_HOST));
        Assert.assertNull(FieldOption.getFirstFieldOption(list, FieldOptionConstats.OPTION_PORT));
    }
}
