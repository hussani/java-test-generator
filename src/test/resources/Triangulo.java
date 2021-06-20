package codigos;

public class Triangulo {

    public static String classificaTriangulo(int LA, int LB, int LC) throws LadoInvalidoException{
        String resposta="";
        if (LA<=0 || LB <=0 || LC <=0)
            throw new LadoInvalidoException("lado invalido");

        if ( (LA >= LB + LC) || (LB >= LA + LC) || (LC >= LA + LB))
            resposta = "NAO FORMA TRIANGULO";
        else
        {
            if (LA==LB && LB==LC)
                resposta = "EQUILATERO";
            else
            {
                if (LA==LB || LB==LC || LA==LC)
                    resposta = "ISOSCELES";
                else
                    resposta = "ESCALENO";
            }

        }
        return resposta;
    }
}
