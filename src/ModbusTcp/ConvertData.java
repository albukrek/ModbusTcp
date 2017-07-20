package ModbusTcp;

public class ConvertData {

    public long toLong(int lowReg, int highReg){
        return lowReg<<16|highReg;
    }

    public float toFloat(int lowReg , int highReg){
        UnionNumber union =new UnionNumber();
        union.asInt.set(lowReg<<16| highReg);
        return union.asFloat.get();
    }

}

