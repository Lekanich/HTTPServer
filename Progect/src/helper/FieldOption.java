package helper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 25.08.13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class FieldOption {

    private byte kindOption;
    private Object option;

    private FieldOption(String field){
        String[] lines = field.replace(' ',';').split(";");
        if(lines.length == 3){
            setKindOption(lines[0]);
            //ignore lines[1], it was created for user's comfort.
            setOption(lines[2]);
        }else{
            kindOption = FieldOptionConstats.OPTION_BAD;
        }
    }

    private void setKindOption(String kind){
        try{
            switch (Integer.parseInt(kind)){
                case FieldOptionConstats.OPTION_HOST: this.kindOption = FieldOptionConstats.OPTION_HOST;  break;
                case FieldOptionConstats.OPTION_PORT: this.kindOption = FieldOptionConstats.OPTION_PORT;  break;
                default: this.kindOption = FieldOptionConstats.OPTION_BAD;
            }
        }catch(NumberFormatException nfe){
            this.kindOption = FieldOptionConstats.OPTION_BAD;
        }
    }

    private void setOption(String option){
        switch(kindOption) {
            case FieldOptionConstats.OPTION_HOST : this.option = option; return;
            case FieldOptionConstats.OPTION_PORT : this.option = new Integer(option); return;
            default : return;
        }
    }

    public Object getOption(){
        return option;
    }

    @Override
    public boolean equals(Object obj){
        FieldOption fo = (FieldOption)obj;
        return (fo.kindOption == this.kindOption && fo.option.equals(this.option)) ? true : false;
    }

    public static FieldOption getInstance(String line){
        FieldOption fo = new FieldOption(line);
        return fo.kindOption == FieldOptionConstats.OPTION_BAD ? null : fo;
    }

    public static FieldOption getFirstFieldOption(List<FieldOption> list, byte kindOption){
        for(FieldOption fo : list){
            if( fo!=null && fo.kindOption == kindOption)
                return fo;
        }
        return null;
    }
}
