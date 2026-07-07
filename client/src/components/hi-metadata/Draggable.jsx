import React from 'react';
import { useDraggable } from '@dnd-kit/core';
// This component is not used.
export function Draggable(props) {
    const { attributes, listeners, setNodeRef, transform } = useDraggable({
        id: props.id,
    });
    const style = transform ? {
        transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
    } : undefined;


    return (
        <button data-testid = "hi-metadata-draggable" ref={setNodeRef} style={style} {...listeners} {...attributes}>
            {props.children}
        </button>
    );
}