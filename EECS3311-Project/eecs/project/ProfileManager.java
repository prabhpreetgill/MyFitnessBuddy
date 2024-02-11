package eecs.project;

public class ProfileManager {
    static Profile selectedProfile = null;

    public static void setProfile(Profile profile) {
        selectedProfile = profile;
    }

    public static Profile getProfile() {
        return selectedProfile;
    }
}
