function drawAreaGraphWithPoints(canvas, lastR, allResults) {
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const scale = 40;
    const tickLength = 8;

    ctx.clearRect(0, 0, width, height);

    ctx.strokeStyle = 'rgba(0, 0, 0, 0.1)';
    ctx.lineWidth = 0.5;
    for (let i = -10; i <= 10; i++) {
        const x = centerX + i * scale / 2;
        ctx.beginPath();
        ctx.moveTo(x, centerY - scale * 5);
        ctx.lineTo(x, centerY + scale * 5);
        ctx.stroke();
    }
    for (let i = -10; i <= 10; i++) {
        const y = centerY - i * scale / 2;
        ctx.beginPath();
        ctx.moveTo(centerX - scale * 5, y);
        ctx.lineTo(centerX + scale * 5, y);
        ctx.stroke();
    }
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(centerX - scale * 5, centerY);
    ctx.lineTo(centerX + scale * 5, centerY);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(centerX, centerY - scale * 5);
    ctx.lineTo(centerX, centerY + scale * 5);
    ctx.stroke();
    ctx.fillStyle = '#000';
    ctx.beginPath();
    ctx.moveTo(centerX + scale * 5 - 10, centerY - 5);
    ctx.lineTo(centerX + scale * 5, centerY);
    ctx.lineTo(centerX + scale * 5 - 10, centerY + 5);
    ctx.fill();
    ctx.beginPath();
    ctx.moveTo(centerX - 5, centerY - scale * 5 + 10);
    ctx.lineTo(centerX, centerY - scale * 5);
    ctx.lineTo(centerX + 5, centerY - scale * 5 + 10);
    ctx.fill();
    ctx.font = '14px Arial';
    ctx.fillText('X', centerX + scale * 5 + 5, centerY - 10);
    ctx.fillText('Y', centerX + 10, centerY - scale * 5 - 10);
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1;
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    for (let i = -5; i <= 5; i++) {
        if (i === 0) continue;

        const x = centerX + i * scale;
        ctx.beginPath();
        ctx.moveTo(x, centerY - tickLength);
        ctx.lineTo(x, centerY + tickLength);
        ctx.stroke();
        ctx.fillText(i.toString(), x, centerY + 15);
    }

    for (let i = -5; i <= 5; i++) {
        if (i === 0) continue;

        const y = centerY - i * scale;
        ctx.beginPath();
        ctx.moveTo(centerX - tickLength, y);
        ctx.lineTo(centerX + tickLength, y);
        ctx.stroke();
        ctx.fillText(i.toString(), centerX - 15, y);
    }

    ctx.fillStyle = 'rgba(200, 200, 200, 0.3)'; // Серый полупрозрачный
    const y3 = centerY - 3 * scale; // Y = 3 (верхняя граница)
    const yMinus3 = centerY + 3 * scale; // Y = -3 (нижняя граница)
    ctx.fillRect(centerX - scale * 5, y3, scale * 10, yMinus3 - y3)

    ctx.fillText('0', centerX - 12, centerY + 12);
    ctx.fillStyle = 'rgba(0, 123, 255, 0.3)';
    ctx.beginPath();
    ctx.arc(centerX, centerY, scale * lastR, Math.PI * 1.5, Math.PI * 2);
    ctx.lineTo(centerX, centerY);
    ctx.closePath();
    ctx.fill();
    ctx.beginPath();

    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX + scale * lastR, centerY);
    ctx.lineTo(centerX, centerY + scale * lastR * 0.5);
    ctx.closePath();
    ctx.fill();

    ctx.fillRect(centerX - scale * lastR * 0.5, centerY, scale * lastR * 0.5, scale * lastR);

    if (Array.isArray(allResults)) {
        allResults.forEach(point => {
            const px = centerX + point.x * scale;
            const py = centerY - point.y * scale;
            ctx.fillStyle = point.hit ? 'green' : 'red';
            ctx.beginPath();
            ctx.arc(px, py, 5, 0, 2 * Math.PI);
            ctx.fill();
            ctx.strokeStyle = 'white';
            ctx.lineWidth = 1;
            ctx.stroke();
        });
    }
}