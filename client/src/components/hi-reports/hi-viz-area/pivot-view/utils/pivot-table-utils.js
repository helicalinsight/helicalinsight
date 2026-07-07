export const getLastNode = (node) => {
    if (!node.children.length) return node
      for (let i = 0; i < node.children.length; i++){
          if (node.children[i].className === "e-collapse e-icons") continue
          return getLastNode(node.children[i])
      }
  }

  export const isAggregatedTitle = (text) => {
    if (text) {
        text = text.split(" ") || []
        if (text.length > 3 && text[0]==="Total" && ["Sum", "Avg", "Min", "Max", "Count"].includes(text[1]) && text[2] === "of") {
            return {isTrue: true, text: text.slice(3).join(" ")}
        }
        return {isTrue: false, text: ""}
    } 
    return {isTrue: false, text: ""}
  }
  
export const changeExapandSettings = (fields, data) => {
    if (!fields) return [];
    return fields?.map(({ name }) => {
        return {
            name,
            items: data?.reduce((prev, next) => {
                if (next[name] && !prev?.includes(next[name])) {
                    prev.push(next[name])
                }
                return prev;
            }, [])
        }
    })
}