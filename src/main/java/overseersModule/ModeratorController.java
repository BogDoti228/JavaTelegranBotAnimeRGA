package overseersModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum ModeratorController {
    MODERATOR_CONTROLLER;
    private final ArrayList<Long> moderatorsList = new ArrayList<>(){{
        add((long) 364198280);
        add((long) 341332628);
    }
    };

    private final Map<Long, Boolean> moderatorsInCheckMode = new HashMap<>(){{
        put((long) 364198280, false);
        put((long) 341332628, false);
    }};


    public Boolean isUserModerator(Long user){
        return moderatorsList.contains(user);
    }

    public void openCheckMode(Long user){
        moderatorsInCheckMode.put(user, true);
    }

    public void closeCheckMode(Long user){
        moderatorsInCheckMode.put(user, false);
    }

    public Boolean isModeratorInCheckMode(Long user){
        return moderatorsInCheckMode.get(user);
    }
}
