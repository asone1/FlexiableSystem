// import { createElement } from "react";
import React, { useState } from 'react';
import ReactDOM from 'react-dom';

export default function SelectableCell({myObject}) {
    // const styles = new Map([[1, { color: 'blue',backgroundColor:'yello' }], [0, { color: 'black' }]])
    const styles = [ { color: 'blue',backgroundColor:'yello' },  { color: 'black' }];
    var myCondition = 0;
    const [condition, setCondition] = useState(myCondition);
    
    return (<div
        id={myObject.id}
        style={styles[condition]}
        onClick={()=>setCondition(Number(!(!!condition)))}> 
        {myObject.name}</div>)

}
