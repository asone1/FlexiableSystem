import React, { useState,createContext } from 'react';
import { getObjectFromDB } from '../DataFromDB';
import MyWindow from './MyWindow';
import NestedTable from './NestedTable';

export const DataGroup = createContext();
// export const TableName = createContext();

export default function MainWindow({path}) {
    const [data, setData] = useState([]);

    React.useEffect(() => {
        async function getData() {
            const d = await  getObjectFromDB(path);
            console.log('MainWindow')
            console.log(d)
           setData(d);
        }
        getData();
    }, []);
//<MyInput id="kkk" value="test"/>
    return  <DataGroup.Provider value={{ data, setData }}> {Array.isArray(data)? <><NestedTable/></> : <MyWindow/>}<button onClick={()=>{console.log(data)}}>click</button></DataGroup.Provider>;
}
