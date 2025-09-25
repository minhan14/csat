import React, { useState, useEffect, useMemo } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// --- Helper Components ---

const StatCard = ({ title, value, icon, color }) => (
    <div className="bg-white p-6 rounded-lg shadow-md flex items-center">
        <div className={`w-12 h-12 rounded-full flex items-center justify-center ${color} mr-4`}>
            {icon}
        </div>
        <div>
            <p className="text-gray-500 text-sm">{title}</p>
            <p className="text-2xl font-bold">{value}</p>
        </div>
    </div>
);

const Dashboard = ({ token, onLogout }) => {
    const [feedback, setFeedback] = useState([]);
    const [branches, setBranches] = useState([]); // Will now be populated from /api/branches
    const [selectedBranch, setSelectedBranch] = useState('');
    const [selectedRating, setSelectedRating] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const API_URL = 'http://localhost:8080/api';

    // NEW: useEffect to fetch the list of branches once on component mount
    useEffect(() => {
        const fetchBranches = async () => {
            try {
                const response = await fetch(`${API_URL}/branches`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        onLogout(); // Log out if token is invalid
                    }
                    throw new Error('Failed to fetch branches');
                }
                const data = await response.json();
                setBranches(data);
            } catch (err) {
                setError(err.message);
            }
        };

        fetchBranches();
    }, [token, onLogout]); // Depends on token to make authenticated request

    // This function now ONLY fetches feedback
    const fetchFeedback = async () => {
        setIsLoading(true);
        let url = `${API_URL}/feedback`;
        const params = new URLSearchParams();
        if (selectedBranch) params.append('branchId', selectedBranch);
        if (selectedRating) params.append('rating', selectedRating);

        const queryString = params.toString();
        if (queryString) {
            url += `?${queryString}`;
        }

        try {
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!response.ok) {
                 if(response.status === 401 || response.status === 403){
                    onLogout();
                 }
                 throw new Error(`Failed to fetch feedback: ${response.statusText}`);
            }
            const data = await response.json();
            setFeedback(data);
            // REMOVED: The old logic that derived branches from feedback is gone.
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    // This useEffect now correctly re-fetches feedback whenever filters change.
    useEffect(() => {
        fetchFeedback();
    }, [selectedBranch, selectedRating, token]);

    // This calculation logic remains the same
    const { overallScore, totalFeedback, ratingDistribution, chartData } = useMemo(() => {
        if (feedback.length === 0) {
            return { overallScore: 'N/A', totalFeedback: 0, ratingDistribution: {}, chartData: [] };
        }
        const totalScore = feedback.reduce((acc, item) => acc + item.rating, 0);
        const score = (totalScore / feedback.length).toFixed(2);
        const distribution = feedback.reduce((acc, item) => {
            acc[item.rating] = (acc[item.rating] || 0) + 1;
            return acc;
        }, {});
        const data = [
            { name: '1 Star', count: distribution[1] || 0 },
            { name: '2 Stars', count: distribution[2] || 0 },
            { name: '3 Stars', count: distribution[3] || 0 },
            { name: '4 Stars', count: distribution[4] || 0 },
            { name: '5 Stars', count: distribution[5] || 0 },
        ];
        return { overallScore: score, totalFeedback: feedback.length, ratingDistribution: distribution, chartData: data };
    }, [feedback]);

    return (
        <div className="bg-gray-100 min-h-screen p-4 sm:p-8">
            <header className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-gray-800">CSAT Dashboard</h1>
                <button
                    onClick={onLogout}
                    className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg transition-colors"
                >
                    Logout
                </button>
            </header>
            <main>
                {/* Stat cards remain the same */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                    <StatCard title="Overall CSAT Score" value={overallScore} color="bg-blue-100" icon={<span className="text-2xl">⭐</span>} />
                    <StatCard title="Total Feedback" value={totalFeedback} color="bg-green-100" icon={<span className="text-2xl">📊</span>} />
                    <StatCard title="Positive Ratings (4-5)" value={(ratingDistribution[4] || 0) + (ratingDistribution[5] || 0)} color="bg-yellow-100" icon={<span className="text-2xl">😊</span>} />
                </div>

                <div className="bg-white p-6 rounded-lg shadow-md">
                    {/* Filter section now uses the dedicated 'branches' state */}
                    <div className="flex flex-col sm:flex-row gap-4 mb-6 pb-6 border-b">
                        <h2 className="text-xl font-semibold text-gray-700 self-center">Filter Feedback</h2>
                        <select
                            value={selectedBranch}
                            onChange={(e) => setSelectedBranch(e.target.value)}
                            className="p-2 border rounded-lg bg-gray-50 focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">All Branches</option>
                            {/* The dropdown now correctly iterates over the state populated by the API call */}
                            {branches.map(branch => (
                                <option key={branch.id} value={branch.id}>{branch.name} ({branch.regionName})</option>
                            ))}
                        </select>
                        <select
                            value={selectedRating}
                            onChange={(e) => setSelectedRating(e.target.value)}
                            className="p-2 border rounded-lg bg-gray-50 focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">All Ratings</option>
                            {[5, 4, 3, 2, 1].map(r => <option key={r} value={r}>{r} Star{r > 1 ? 's' : ''}</option>)}
                        </select>
                    </div>

                    {/* Rest of the component remains the same */}
                    {isLoading ? (
                        <div className="text-center p-10">Loading data...</div>
                    ) : error ? (
                        <div className="text-center p-10 text-red-500">{error}</div>
                    ) : (
                        <>
                            <div className="mb-10">
                                <h3 className="text-lg font-semibold text-gray-700 mb-4">Rating Distribution</h3>
                                <div style={{ width: '100%', height: 300 }}>
                                    <ResponsiveContainer>
                                        <BarChart data={chartData}>
                                            <CartesianGrid strokeDasharray="3 3" />
                                            <XAxis dataKey="name" />
                                            <YAxis allowDecimals={false} />
                                            <Tooltip />
                                            <Legend />
                                            <Bar dataKey="count" fill="#4a90e2" />
                                        </BarChart>
                                    </ResponsiveContainer>
                                </div>
                            </div>
                            <div>
                                <h3 className="text-lg font-semibold text-gray-700 mb-4">Detailed Feedback</h3>
                                <div className="overflow-x-auto">
                                    <table className="min-w-full bg-white">
                                        <thead className="bg-gray-200">
                                            <tr>
                                                <th className="py-2 px-4 text-left">Rating</th>
                                                <th className="py-2 px-4 text-left">Comment</th>
                                                <th className="py-2 px-4 text-left">Staff</th>
                                                <th className="py-2 px-4 text-left">Branch</th>
                                                <th className="py-2 px-4 text-left">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {feedback.map(item => (
                                                <tr key={item.id} className="border-b hover:bg-gray-50">
                                                    <td className="py-2 px-4">{item.rating} ⭐</td>
                                                    <td className="py-2 px-4 max-w-sm truncate">{item.comment || '-'}</td>
                                                    <td className="py-2 px-4">{item.staffId}</td>
                                                    <td className="py-2 px-4">{item.branchName}</td>
                                                    <td className="py-2 px-4">{new Date(item.timestamp).toLocaleString()}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </>
                    )}
                </div>
            </main>
        </div>
    );
};

// --- Main App and Login Components (Unchanged) ---

const LoginPage = ({ onLogin, setError, error }) => {
    const [staffId, setStaffId] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ staffId, password }),
            });
            if (response.ok) {
                const data = await response.json();
                onLogin(data.token);
            } else {
                 const errorData = await response.json();
                 setError(errorData.message || 'Invalid staff ID or password');
            }
        } catch (err) {
            setError('Failed to connect to the server.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white rounded-lg shadow-xl w-full max-w-sm">
                <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">Admin Portal Login</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-2" htmlFor="staffId">Staff ID</label>
                        <input
                            id="staffId"
                            type="text"
                            value={staffId}
                            onChange={(e) => setStaffId(e.target.value)}
                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 mb-2" htmlFor="password">Password</label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>
                    {error && <p className="text-red-500 text-sm text-center mb-4">{error}</p>}
                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition-colors disabled:bg-gray-400"
                    >
                        {isLoading ? 'Logging in...' : 'Login'}
                    </button>
                </form>
            </div>
        </div>
    );
};

function App() {
    const [accessToken, setAccessToken] = useState(localStorage.getItem('token'));
    const [error, setError] = useState(null);

    const handleLogin = (newAccessToken) => {
        setAccessToken(newAccessToken);
        localStorage.setItem('accessToken', newAccessToken);
    };

    const handleLogout = () => {
        setAccessToken(null);
        localStorage.removeItem('accessToken');
    };

    return (
        <div>
            {accessToken ? (
                <Dashboard token={accessToken} onLogout={handleLogout} />
            ) : (
                <LoginPage onLogin={handleLogin} setError={setError} error={error} />
            )}
        </div>
    );
}

export default App;

