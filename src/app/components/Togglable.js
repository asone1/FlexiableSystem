import React, { useState, createContext } from 'react';
import { createElement } from 'react';
import ReactDOM from 'react-dom';
import Table from './Table';
import DefaultElement from './DefaultElement';
import { combineOption } from './DefaultElement';

// export const SelectMode = createContext();

export default function Togglable({ thisStyle, children }) {
    
    const [condition, setCondition] = useState(false);

    function toggle() {
        setCondition(!condition);
    }

    return   <>
    { DefaultElement(combineOption(
        {'event':[["onClick",toggle]],'option':{checked:condition, type:"checkbox"}},thisStyle) ) }
    {children? children:null}
</>;
}
//<Table clickable={condition}/>