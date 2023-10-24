import React, { useState, useContext, useRef } from 'react';
import MyInput from './MyInput';
import NewInput from './NewInput';
import DefaultElement from './DefaultElement';
export default function ATable({ id, columns, children }) {
    const [colInfo, setColInfo] = useState(columns);
    const [data, setData] = useState([]);


    React.useEffect(() => {
        setData(children);
    }, [children]);

    React.useEffect(() => {
        setColInfo(columns);
    }, [columns]);

    function createRow(map,tds,isTable) {
        let tr=[];
        Object.keys(map).map((key) => {
            const td = null;
            if (typeof map[key] == 'string') {
                if(!isTable){
                    tds.push(React.createElement("td", { key:key},<NewInput id={key} value={""}/>));
                }else{
                    tr.push(<td key={key}><NewInput id={key} value={""}/></td>);
                }
            }
            else {
                tr = createRow(map[key],tds,true);
                tds.push(<table  key="uuid-new"><tr id="uuid-new" key="uuid-new">{tr}</tr></table>)
            }
        })
        return tr;


    }
    function add() {

        if (typeof colInfo == 'object') {
            let tds = [...data];
            let newtds = [];
            createRow(colInfo,newtds,false)
            tds.push(<tr id="uuid-new" key="uuid-new">{newtds}</tr>)
            setData(tds);
        }

    }

    // return React.createElement("table", {}, React.createElement("tbody", {}, data,React.createElement("tr",{"onClick":add},'add new')));
    return <table id={id}><tbody>{data}<tr><td onClick={() => add()}>Create new</td></tr></tbody></table>
    

}