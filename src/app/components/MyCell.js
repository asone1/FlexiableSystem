import React, { useState, useContext, createContext } from 'react';
import MyInput from './MyInput';
import DefaultElement from './DefaultElement';

//condition could based on value (e.g. color in red when >limit) 
//or column(field) (e.g. fixed if it could not be modified)
export default function MyCell({index,id, value}) {

    //index : [method that return boolean , cell design]
    const columnsCondition = {};
    const nonEditable = new Set();

    function init() {
        console.log("init")
        nonEditable.add(2);
        console.log(nonEditable)
        console.log(nonEditable.has(("3")))
        columnsCondition["2"] = [function test(value){if(value =='hi') return true;else return false},{color:'blue'}];
    }
    //return ReactElement
    function getCell(index,id,value) {
        init();
        var reactEle = { 'id': id, 'value': value }
        console.log("get cell"+ index)
        if (nonEditable.has(index)) {
            console.log("has"+index)
            reactEle["type"] = MyInput;
        }
        console.log("getCell")
        if(index && index.toString()  in columnsCondition){
            console.log("before condit")
            var condition = columnsCondition[index];
            if(Array.isArray(condition)&& condition.length>1){
             var validateMethod = condition[0];
             var style = condition[1];
             console.log("before validate")
             if(validateMethod(value)){
                reactEle["style"]  = style;
             }
            }
        }
        return DefaultElement(reactEle);
    }

    return getCell(index,id, value);
}