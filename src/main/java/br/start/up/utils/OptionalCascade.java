package br.start.up.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class OptionalCascade {

    public List<Option> listOptions = new ArrayList<>();

    public OptionalCascade ofNoNullable(Object input, Object returnValue) {
        listOptions.add(new Option(input, returnValue));
        return this;
    }

    public Object build() {
        for (Option o: listOptions){
            if(o.input != null){
                return o.value;
            }
        }
        return null;
    }

    private class Option {
        public Object input;
        public Object value;

        public Option(Object input, Object value){
            this.input = input;
            this.value = value;
        }
    }
}
