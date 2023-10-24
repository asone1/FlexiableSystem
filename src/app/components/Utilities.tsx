
import { createElement } from 'react';
import React from 'react';
import DefaultElement from './DefaultElement';
import Togglable from './Togglable'
import './css/table.scss';
import MyInput from './MyInput';

export function getArgByIndex(url:string, index:number){
    const query = url.substring(url.indexOf("api")+4,url.length).split("/");
//        System.out.println( url.toString());

        if (query.length > index) {
            return query[index].replace("%20"," ");
        }
        return null;
}

export function getObjectKey(jsonObj: object) {
    if (jsonObj) { return Object.keys(jsonObj); }
    else return [];
}

export function jsonArrToObjArr(JsonArr: Array<object>) {
    if (JsonArr) {
        var temp = [];
        JsonArr.forEach(obj => {
            const properties = [];
            var countKey = 0;
            for (var sub_key in obj) {
                properties[countKey++] = obj[sub_key];
            }
            temp.push(properties);
        });
        return temp;
    }
    else return [];
}

export function cloneJson(data: JSON) {
    var cloned = {};
    Object.keys(data).forEach((key) => {
        if(key==="id") cloned[key] = "new"
        else cloned[key] = "";
    });
    return cloned;
}

export function jsonToRow(DataType: any, data: Array<JSON>) {
    let html = [];
    // console.log(Array.isArray(data))
    if (data && data instanceof Array) {
        data.forEach((jsonObj) => {
            let objIDs = Object.keys(jsonObj);

            objIDs.forEach((objID) => {
                console.log(objID);
                let cells = [];
                let fields = jsonObj[objID];
                fields.forEach(field => {
                    // cells.push(<td><input value={field["fieldValue"]}></input></td>)
                    cells.push(<td id={field["fieldId"]} key={field["fieldId"]}>
                        {DefaultElement({ 'id': objID + '-' + field["fieldId"], 'type': DataType, 'value': field["fieldValue"] })}</td>);
                });
                html.push(<tr key={objID} id={objID} children={cells}></tr>);
                // html.push(<tr key={objID} id={objID} children={cells}></tr>);  
            })
        })
    }
    return html;
}

/*
export function jsonToRows(DataType: any, data: Array<JSON>, checkable: boolean) {
    let html = [];
    // console.log(Array.isArray(data))
    if (data && data instanceof Array) {
        data.forEach((jsonObj) => {
            let it = Object.keys(jsonObj);
            let cells = [];
            let idName = findIDinProp(jsonObj);
            let idValue = jsonObj[idName];

            it.forEach((key) => {
                if (ifContainIDInRow(key)) {
                    if (checkable) {
                        cells.push(<Togglable thisStyle={{ 'type': 'input' }} children={null} />)
                    }
                    //  cells.push(<td key={jsonObj[key]}><DataType myObject={jsonObj[key]} /></td>);
                    cells.push(<td key={jsonObj[key]}>{DefaultElement({ 'type': DataType, 'value': jsonObj[key] })}</td>);
                }
            })
            // html.push(<tr className='table-header' key={idValue} id={idValue}>{cells}</tr>);
            html.push(<MyRow key={idValue} id={idValue} children={cells}></MyRow>);
        })
    }
    return html;
}*/

 const notBase64 = /[^A-Z0-9+\/=]/i;
export function isBase64(str:string) {
    // assertString(str); // remove this line and make sure you pass in a string
    const len = str.length;

    if(len > 100) return true;
    if (!len || len % 4 !== 0 || notBase64.test(str)) {
          return false;
    }
    if(!Number.isNaN(str)) {return false;}
    const firstPaddingChar = str.indexOf('=');
    return firstPaddingChar === -1 ||
      firstPaddingChar === len - 1 ||
      (firstPaddingChar === len - 2 && str[len - 1] === '=');
      
  }

  export function mapToARow(DataType: any, map: any) {
    let resultRow = [];
    Object.keys(map).forEach(key => {
        resultRow.push(DefaultElement({ 'type': DataType, 'value': map[key] ,'id':key,'key':key}))
    });
    return resultRow;
  }
export function arrToARow(DataType: any, arr: Array<any>) {
    
    let resultRow = [];
    
    const header = arr.map((ele) => {delete ele['fieldId']; return ele})

    if (typeof header[0] === 'string') {
    
        header.forEach((cell) => {
            if (ifContainIDInRow(cell)) {
                console.log(cell)
                resultRow.push(DefaultElement({ 'type': DataType, 'value': cell }))
            }
        });
    }else {
        header.forEach((cell)=>{
            resultRow.push(<th key={ cell['fieldId']}>{ cell['fieldName']}</th>)
        })
       
            // resultRow.push(DefaultElement({  'type': DataType, 'value': header[Object.keys(header)[0]] }))
        
    }
    return <tr>{resultRow}</tr>;
}

export function getColumnName(arr: Array<any>, index: number) {
    return arr.at(index);
}

function ifContainIDInRow(data: string) {
    return !data.toLowerCase().includes('id');
}

export function gethtmlasList(elements, htmlEleTyp) {

    const eles = [];

    var index = 0;

    if (typeof htmlEleTyp === 'string') {

        elements.forEach((element) => {
            eles.push(createElement(htmlEleTyp, { key: (index++).toString() }, element));

        });

    } else {

        elements.forEach((element) => {
            eles.push(createElement(htmlEleTyp, { key: (index++).toString(), value: element }));

        });

    }




    return eles

}

export function findIDinProp(data: Object) {
    var value = Object.keys(data).find((key) => { return key.toLowerCase().includes("id") });
    if (value !== undefined) return value;
    else return "";
}


function renameKey(obj: Object, oldKey: string, newKey: string) {
    obj[newKey] = obj[oldKey];
    delete obj[oldKey];
}

function processData(JsonArr: Array<Object>) {
    if (JsonArr.length > 0) {
        var value = findIDinProp(JsonArr[0]);
        JsonArr.forEach((element) => {
            renameKey(element, value, 'id');
        })
    }
    return JsonArr;
}

/*
return   axios.request<ServerData>({
    url: 'http://localhost:8081/api/class/' + className,
    transformResponse: (r: ServerResponse) => r.data
}).then(  (response) => {
    // `response` is of type `AxiosResponse<ServerData>`
    const { data } = response
    console.log('inside get:'+ data)
    return data;
    // `data` is of type ServerData, correctly inferred
})*/


