package tn.esprit.entities;

public class Session {
    private static Personne user;
    private static Session session;
    private Session(Personne user){
            this.user = user;
    }
    public static Personne getUser() {
        return user;
    }
    public static Session initSession(Personne user){ //login
        if(session == null){
            session = new Session(user);
        }
        return session;
    }
    public static Session getSession(){ //autre controller / component
        return session;
    }
    public static void destroySession() {
        session = null;
    }

}
