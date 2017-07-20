package ModbusTcp;

import javolution.io.Union;

public class UnionNumber extends Union{
    Signed32   asInt    = new Signed32();
    Float32    asFloat  = new Float32();
}
