
export const createFn = function(fnParams: any) {
  return (...args) => {
    const params = fnParams.params.map((arg, i) => args[i]);

    return new Function(...fnParams.params, 'additionalParams', fnParams.functionBody)(...params);
  };
};

export function findObjects(obj, targetProp, targetValue, results) {

  function getObject(theObject, parentObject?) {
    const result = null;
    if (theObject instanceof Array) {
      for (let i = 0; i < theObject.length; i++) {
        getObject(theObject[i], theObject);
      }
    } else {
      for (const prop in theObject) {
        if (theObject.hasOwnProperty(prop)) {
          if (prop === targetProp) {
            results.push(parentObject);
          }
          if (theObject[prop] instanceof Object || theObject[prop] instanceof Array) {
            getObject(theObject[prop], theObject);
          }
        }
      }
    }
  }

  getObject(obj);
}
