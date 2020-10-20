package overseersModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModeratorController {
    private static final ArrayList<Long> moderatorsList = new ArrayList<>(){{
        add((long) 364198280);
        add((long) 341332628);
    }
    };

    private static final Map<Long, Boolean> moderatorsInCheckMode = new HashMap<>(){{
        put((long) 364198280, false);
        put((long) 341332628, false);
    }};


    public static Boolean isUserModerator(Long user){
        return moderatorsList.contains(user);
    }

    public static void openCheckMode(Long user){
        moderatorsInCheckMode.put(user, true);
    }

    public static void closeCheckMode(Long user){
        moderatorsInCheckMode.put(user, false);
    }

    public static Boolean isModeratorInCheckMode(Long user){
        return moderatorsInCheckMode.get(user);
    }
}
