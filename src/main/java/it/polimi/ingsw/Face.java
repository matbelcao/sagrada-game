package it.polimi.ingsw;


import org.jetbrains.annotations.Contract;

public enum Face {
    ONE(1,"\u2680"),
    TWO(2,"\u2681"),
    THREE(3,"\u2682"),
    FOUR(4,"\u2683"),
    FIVE(5,"\u2684"),
    SIX(6,"\u2685");

    private final int number;
    private final String ascii;
    static final String EMPTY = "\u25a0"; //to be used in color restrictions

    Face(final int number, final String ascii){
        this.number=number;
        this.ascii=ascii;
    }

    @Contract(pure = true)
    public Face getFace(int number) throws InvalidFaceValueException {
        switch(number) {
            case 1:
                return Face.ONE;
            case 2:
                return Face.TWO;
            case 3:
                return Face.THREE;
            case 4:
                return Face.FOUR;
            case 5:
                return Face.FIVE;
            case 6:
                return Face.SIX;
            default :
                throw new InvalidFaceValueException();

        }
    }

    @Contract(pure = true)
    public int value(){
        return this.number;
    }
    @Contract(pure = true)
    public String ascii(){
        return this.ascii;
    }

    public static boolean contains(String number) {

        for (Face c : Face.values()) {
            if (c.toString().equals(number)) {
                return true;
            }
        }

        return false;
    }
}
