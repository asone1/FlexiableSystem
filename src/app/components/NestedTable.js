import React, { useState, useContext, createContext } from 'react';
import { getObjectFromDB, createObject } from '../DataFromDB';
import './css/table.scss';
import './css/button.css';
import Row from './Row';
import { DataGroup } from './MainWindow';
import { useParams } from 'react-router';
import { mapToARow, cloneJson } from './Utilities';
import ATable from './ATable';
import DefaultElement from "./DefaultElement";
import MyInput from "./MyInput";
import MyCell from './MyCell';
// export const CreateChanges = createContext();
export const UnsavedChanges = createContext({});

export default function NestedTable() {


    const { tableName } = useParams();
    const [colHead, setColHead] = useState([]);
    const [totalTable, setTotalTable] = useState([]);
    const [mydata, setMydata] = useState([]);
    const [sizeMap, setSizeMap] = useState({});
    const [allChanges, setAllChanges] = useState({});
    const [createChanges, setCreateChanges] = useState({});
    const { data, setData } = useContext(DataGroup);
    // const elementsRef = useRef(data.map(() => createRef()));

    React.useEffect(() => {
        async function getColumns() {
            const h = await getObjectFromDB("fields");
            let headerMap = {};
            headerMap = h;
            setColHead(headerMap);

        }
        getColumns();
    }, []);

    React.useEffect(() => {
        setMydata(data);
        // setTotalTable(getTotal(JSON.stringify(data)));

        let countLevel = -1;
        let tempMap = {};
        function calculateSizeMap(map) {
            if (Array.isArray(map)) {
                ++countLevel;
                let size = 0;
                map.forEach((e) => {
                    size = calculateSizeMap(e)
                });
                tempMap[countLevel] = size;
                --countLevel;

            }
            else if (typeof map == 'object') {
                let countLevelOfObj = 0;
                Object.keys(map).forEach((key) => {
                    ++countLevelOfObj;
                    if (Array.isArray(map[key])) calculateSizeMap(map[key]);
                });
                return countLevelOfObj
            }
        }
        calculateSizeMap(data);
        setSizeMap(tempMap);
    }, [data]);

    function getMapByIndex(map, idx) {
        let countTable = -1;
        let result = map;
        Object.keys(map).map((key) => {
            // console.log(typeof map[key])
            // console.log(countTable)
            if (typeof map[key] == 'object') {
                if (idx == ++countTable) {
                    result = map[key];
                }
            }
        })
        return result;
    }

    function numberOfObject(object) {

        let stringObj = JSON.stringify(object);
        let arr = stringObj.split("{");
        return arr.length > 0 ? arr.length - 1 : 0;
    }

    let countTable = 0;
    let columnTables = [];
    let countNest = 0;

    //to store the size of objects according to its level (number of table)
    /*structure of nested table:
    <table id ={0:{fieldID: fieldName ...}, 1:child2Table, n:class field table }>
     <thead>
        <th>...
        <th>...
        <th><table><thead>...
     </thead>
    </table>
    <table>
     <tbody>
        <tr><td>...
        <tr><td>...
        <tr><td><table>...
     </tbody>
    */
    function getNestedItem(index, id, map, isHead) {

        if (Array.isArray(map) && !isHead) {
            let trs = [];
            map.forEach((ele, idx, array) => {
                const tr = getNestedItem(index, id, ele, isHead)
                trs.push(<tr id={Object.keys(ele)[0]} key={Object.keys(ele)[0]}>{tr}</tr>);
            });

            ++countNest;
            if (countNest == data.length) return <ATable id={countNest} key={id} columns={countNest == data.length ? colHead : getMapByIndex(colHead, countNest - 1)} children={trs} />
            else return <td key={id}  ><ATable id={countNest} columns={countNest == data.length ? colHead : getMapByIndex(colHead, countNest - 1)} children={trs} /></td>
        }
        else if (typeof map == 'string') {
            
            if (isHead) return <th key={id} id={id}>{map}</th>;
            // else return <td id={id}>{map}</td>;
            // else return <td key={id}>{DefaultElement({ 'id': id, 'type': MyInput, 'value': map })}</td>
            
            else return <td key={id}><MyCell index={index} id={id} value ={map}/></td>
        }
        else if (typeof map == 'object') {

            let cellCount =0;
            let ids = Object.keys(map);
            let cell = [];
            let tableInfo = {};
            if (!isHead) {
                countNest = 0;
            }
            ids.forEach((id) => {
                let value = map[id];
                let td = getNestedItem(++cellCount, id, value, isHead);
                console.log("in nested:"+cellCount)
                console.log("in nested:"+value)
                if (isHead && typeof value == 'string') {
                    tableInfo[id] = value;
                }
                cell.push(td);
            })
            if (isHead) {
                columnTables[countTable++] = tableInfo;
                if (countTable == numberOfObject(colHead)) return <table id={countTable} key={countTable}><thead><tr key="header">{cell}</tr></thead></table>
                else return <th key={countTable}><table id={countTable} key={countTable}><thead><tr key="header">{cell}</tr></thead></table></th>
            }
            else {
                return cell;
            }
        }
    }
    function save() {
        // console.log(allChanges); 
        // console.log(createChanges);
        createObject("http://localhost:8081/api/myfield/Order", JSON.stringify(createChanges))
    }

    return <UnsavedChanges.Provider value={{ allChanges, createChanges }}>
        <button onClick={() => save()}>save</button>{getNestedItem(0,"head", colHead, true)}{getNestedItem(0,"order", mydata, false)}
    </UnsavedChanges.Provider>;
}
