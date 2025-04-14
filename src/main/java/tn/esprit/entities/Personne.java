package tn.esprit.entities;



public class Personne {
    //attributes
    private Integer id;
    private String email;
    private String roles;
    private String password;
    private String last_Name;
    private String first_Name;
    private String num_tel;
    private String address;
    private String verificationCode;
    private String userAgent;
    private String specialite;
    private Integer point;
    private String cin;
    private Personne auth_code = null;
    private int is_verified;//dans symfony il est boolean
    /*- -*/

    //constructors
    public Personne() {
    }

    public Personne(String email, String roles, String password, String cin, String last_Name, String first_Name, String num_tel, String address, Personne auth_code, int
            is_verified) {
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.cin = cin;
        this.last_Name = last_Name;
        this.first_Name = first_Name;
        this.num_tel = num_tel;
        this.address = address;
        this.auth_code = auth_code;
        this.is_verified = is_verified;
    }


    // getters & setters :
    //email
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    //roles
    public String getRoles()
    {
        return roles;
    }

    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    //password
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    //cin
    public String getCin()
    {
        return cin;
    }

    public void setCin(String cin)
    {
        this.cin = cin;
    }

    //lastName
    public String getLast_Name()
    {
        return last_Name;
    }
    public void setLast_Name(String last_Name)
    {
        this.last_Name = last_Name;
    }

    //firstaname
    public String getFirst_Name()
    {
        return first_Name;
    }
    public void setFirst_Name(String first_Name)
    {
        this.first_Name = first_Name;
    }

    //numtel
    public String getNum_tel()
    {
        return num_tel;
    }
    public void setNum_tel(String num_tel)
    {
        this.num_tel = num_tel;
    }

    //address
    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    //authcode
    public Personne getAuth_code()
    {
        return auth_code;
    }
    public void setAuth_code(Personne auth_code)
    {
        this.auth_code = auth_code;
    }

    //isverified
    public int getIs_verified()
    {
        return is_verified;
    }
    public void setIs_verified(int is_verified)
    {
        this.is_verified = is_verified;
    }

    //verificationcode


    public String getVerificationCode() {
        return verificationCode;
    }
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    //id
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;

    }

    //useragent
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    //spacialite
    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    //point
    public Integer getPoint() {
        return point;
    }
    public void setPoint(Integer point) {
        this.point = point;
    }



}
