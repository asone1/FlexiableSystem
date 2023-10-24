import React, { useState,useContext } from 'react';
import "./css/datatip.css"
import { UnsavedChanges } from './NestedTable';

export default function NewInput({id}) {

    const [text, setText] = useState();

    const {createChanges}=useContext(UnsavedChanges);

    function inputChangeHandler(newValue){
        // console.log(allChanges)
        setText(newValue);
        createChanges[id] = newValue;
    }
    
    return  <input style={{color:"red"}} id={id}  onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input>
    // if(text)  return  <input id={id} onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input>
    // else return  <div data-tip={message_noEmpty}><input  id={id} onChange={(e)=>{inputChangeHandler(e.target.value)}} value={text}></input></div>
}
