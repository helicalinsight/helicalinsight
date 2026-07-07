import React from 'react';
import { useDroppable } from '@dnd-kit/core';
// This component is not used.
export function Droppable(props) {
    const { isOver, setNodeRef } = useDroppable({
        id: props.id,
    });
    const style = {
        color: isOver ? 'green' : undefined,
    };


    return (
        <div data-testid = "hi-metadata-droppable" ref={setNodeRef} style={style}>
            {props.children}
        </div>
    );
}