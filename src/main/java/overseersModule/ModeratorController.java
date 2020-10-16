package overseersModule;

import java.util.ArrayList;

public class ModeratorController {
    private static final ArrayList<Long> moderatorsList = new ArrayList<>(){{
        add((long) 364198280);
    }
    };

    public static Boolean isUserModerator(Long user){
        return moderatorsList.contains(user);
    }
}
