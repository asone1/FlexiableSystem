import React, { useState } from 'react';
import { createElement } from 'react';
import ReactDOM from 'react-dom';

export function combineOption(thisStyle, newElement) {
    // console.log( JSON.stringify (Object.assign({}, thisStyle,newElement)))
    return Object.assign({}, thisStyle, newElement);
}

export default function DefaultElement(thisStyle) {

    // let eventStyle1=[["onChange",setText],["onBlur",toggle]];
    // let eventStyle2=[["onClick",toggle]];
    let htmlType = thisStyle['type'];

    //   console.log(JSON.stringify(thisStyle))
    if (typeof htmlType === 'string' || htmlType instanceof String) {
        return createElement(htmlType,
            (() => {

                let option = thisStyle['option'] ? thisStyle['option'] : {};
                htmlType === 'input' ? option['value'] = thisStyle['value'] : {};
                htmlType === 'input' ? option['autoFocus'] = true : {};
                thisStyle['key'] ? option['key'] = thisStyle['key'] : {};
                thisStyle['id'] ? option['id'] = thisStyle['id'] : {};
                thisStyle['style'] ? option['style'] = thisStyle['style'] : {};
                thisStyle['event'] ? thisStyle['event'].forEach((eventArr) => {
                    if (Array.isArray(eventArr) && eventArr.length > 1)
                        var eventType = eventArr[0];
                    var eventHandler = eventArr[1];
                    if (eventType !== null && eventHandler !== null) {
                        option[eventType] = eventHandler;
                    }
                }) : {}
                return option;
            })(), htmlType === 'input' ? null : thisStyle['value']
        )
    } else {
        return createElement(htmlType, {value: thisStyle['value'],id: thisStyle['id']}, thisStyle['value'] ? thisStyle['value'] : '');
    }


}