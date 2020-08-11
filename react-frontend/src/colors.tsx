interface HashTable<T> {
    [key: string]: T;
}

export const getColorCode = (id: string): string => {
    
    switch(id){
        case "gold":
            return "#FFC300" //yellow
        case "indium":
            return "#ABB2B9" //grey
        case "lithium":
            return "#2ECC71" //green
        case "zinn":
            return "#E74C3C" //red
        default:
            return "#FDFEFE" //white
    }
}