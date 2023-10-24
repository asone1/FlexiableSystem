import React, { useState,useContext } from 'react';
import "./css/datatip.css"
import { UnsavedChanges } from './NestedTable';

export default function MyInput({id,value}) {

    const original_value = value? value:"";
    const [text, setText] = useState(original_value);
    const message_noEmpty ="Field cannot be empty!"

    // const { allChanges, setAllChanges } =useContext(UnsavedChanges);
    const {allChanges}=useContext(UnsavedChanges);

    function inputChangeHandler(newValue){
        // console.log(allChanges)
        setText(newValue);
        if(newValue!= original_value){
            allChanges[id] = newValue;
        }
        else{
            delete allChanges[id];
        }
        
    }
    if (text) return <input id={id}  onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input>
    else return  <input id={id}  alt={message_noEmpty} onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input>
    // if(text)  return  <input id={id} onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input>
    // else return  <div data-tip={message_noEmpty}><input  id={id} onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input></div>
}
