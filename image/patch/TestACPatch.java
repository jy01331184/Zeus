package patch;

import com.zeus.Keep;
import com.zeus.Patch;

/**
 * Created by tianyang on 18/4/27.
 */
@Keep
public class TestACPatch {

    @Patch("com.app.TestAC:private void com.app.TestAC.a()")
    private void a(){
        System.out.println("hook fact!");
    }
}
