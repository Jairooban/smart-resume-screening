/**
 * SMART RESUME SCREENING SYSTEM - CLIENT-SIDE APPLICATION
 * Vanilla JavaScript Single Page Application
 */

const API_BASE = '/api';

// ===================================================================
// API CLIENT UTILITY
// ===================================================================
const api = {
    async get(endpoint) {
        try {
            const res = await fetch(`${API_BASE}${endpoint}`);
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'API request failed');
            return data;
        } catch (err) {
            console.error('API GET Error:', err);
            throw err;
        }
    },

    async post(endpoint, body) {
        try {
            const isFormData = body instanceof FormData;
            const headers = isFormData ? {} : { 'Content-Type': 'application/json' };
            const res = await fetch(`${API_BASE}${endpoint}`, {
                method: 'POST',
                headers: headers,
                body: isFormData ? body : JSON.stringify(body)
            });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'API request failed');
            return data;
        } catch (err) {
            console.error('API POST Error:', err);
            throw err;
        }
    },

    async put(endpoint, body) {
        try {
            const res = await fetch(`${API_BASE}${endpoint}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'API request failed');
            return data;
        } catch (err) {
            console.error('API PUT Error:', err);
            throw err;
        }
    },

    async patch(endpoint, body) {
        try {
            const res = await fetch(`${API_BASE}${endpoint}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'API request failed');
            return data;
        } catch (err) {
            console.error('API PATCH Error:', err);
            throw err;
        }
    },

    async delete(endpoint) {
        try {
            const res = await fetch(`${API_BASE}${endpoint}`, {
                method: 'DELETE'
            });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'API request failed');
            return data;
        } catch (err) {
            console.error('API DELETE Error:', err);
            throw err;
        }
    }
};

// ===================================================================
// GLOBAL APP CONTROLLER
// ===================================================================
const app = {
    currentView: 'dashboard',
    charts: {},
    allJobs: [],
    selectedJobId: null,
    currentUser: null,

    init() {
        this.bindEvents();
        this.checkAuth();
        this.handleHashChange();
    },

    bindEvents() {
        window.addEventListener('hashchange', () => this.handleHashChange());

        // Sidebar navigation clicks
        document.querySelectorAll('.sidebar-nav .nav-item[data-view]').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const view = link.getAttribute('data-view');
                window.location.hash = view;
            });
        });

        // Mobile sidebar toggle
        const toggleBtn = document.getElementById('sidebar-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                document.querySelector('.sidebar').classList.toggle('open');
            });
        }
    },

    async checkAuth() {
        try {
            const res = await api.get('/auth/me');
            if (res.success && res.data) {
                this.currentUser = res.data;
                document.getElementById('user-display-name').textContent = res.data.fullName;
                document.getElementById('user-display-role').textContent = res.data.email;
                document.getElementById('user-avatar').textContent = res.data.fullName.substring(0, 2).toUpperCase();
            }
        } catch (e) {
            console.log('Using default session');
        }
    },

    handleAuthAction() {
        if (this.currentUser) {
            this.showToast('Logged out successfully', 'info');
            this.currentUser = null;
            document.getElementById('user-display-name').textContent = 'Guest HR';
            document.getElementById('user-display-role').textContent = 'Click to login';
            document.getElementById('btn-auth-toggle').innerHTML = '<i class="fa-solid fa-right-to-bracket"></i> Login';
        } else {
            this.openModal('login-modal');
        }
    },

    handleHashChange() {
        const hash = window.location.hash.replace('#', '') || 'dashboard';
        this.switchView(hash);
    },

    switchView(viewName) {
        this.currentView = viewName;

        // Update active nav
        document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => {
            item.classList.toggle('active', item.getAttribute('data-view') === viewName);
        });

        // Update view panels
        document.querySelectorAll('.view-panel').forEach(panel => {
            panel.classList.toggle('active', panel.id === `view-${viewName}`);
        });

        // Update top bar titles
        const titles = {
            dashboard: { title: 'Dashboard', subtitle: 'System metrics, automated match scoring, and candidate analytics' },
            jobs: { title: 'Job Openings', subtitle: 'Manage job criteria, mandatory skills, and requirements' },
            upload: { title: 'Upload Resumes', subtitle: 'Upload candidate PDF/DOCX resumes for automated parsing & scoring' },
            ranking: { title: 'Candidate Ranking', subtitle: 'Ranked candidates, score breakdowns, and shortlist management' },
            compare: { title: 'Compare Candidates', subtitle: 'Side-by-side radar and matrix analysis of top candidates' },
            skills: { title: 'Skills Catalog', subtitle: 'Curated 200+ technology and domain skills dictionary' }
        };

        if (titles[viewName]) {
            document.getElementById('view-title').textContent = titles[viewName].title;
            document.getElementById('view-subtitle').textContent = titles[viewName].subtitle;
        }

        // Close mobile sidebar if open
        document.querySelector('.sidebar').classList.remove('open');

        // Load view specific data
        this.loadViewData(viewName);
    },

    refreshCurrentView() {
        this.loadViewData(this.currentView);
        this.showToast('Data refreshed', 'info');
    },

    loadViewData(viewName) {
        if (viewName === 'dashboard') dashboardModule.load();
        else if (viewName === 'jobs') jobsModule.load();
        else if (viewName === 'upload') uploadModule.load();
        else if (viewName === 'ranking') screeningModule.load();
        else if (viewName === 'compare') compareModule.load();
        else if (viewName === 'skills') skillsModule.load();
    },

    openModal(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.add('active');
    },

    closeModal(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.remove('active');
    },

    openNewJobModal() {
        document.getElementById('job-form').reset();
        document.getElementById('job-id').value = '';
        document.getElementById('job-modal-title').innerHTML = '<i class="fa-solid fa-briefcase"></i> Create Job Opening';
        this.openModal('job-form-modal');
    },

    showToast(message, type = 'success') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        
        const icon = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-exclamation' : 'fa-circle-info');
        toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;
        
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }
};

// ===================================================================
// 1. DASHBOARD MODULE
// ===================================================================
const dashboardModule = {
    async load() {
        try {
            const res = await api.get('/dashboard/metrics');
            if (res.success && res.data) {
                this.renderMetrics(res.data);
                this.renderCharts(res.data);
                this.renderTopCandidates(res.data.topCandidates);
            }
        } catch (err) {
            app.showToast('Failed to load dashboard metrics', 'error');
        }
    },

    renderMetrics(m) {
        document.getElementById('stat-total-jobs').textContent = m.totalJobs;
        document.getElementById('stat-active-jobs').textContent = m.activeJobs;
        document.getElementById('stat-total-resumes').textContent = m.totalResumes;
        document.getElementById('stat-avg-score').textContent = `${m.averageMatchScore.toFixed(1)}%`;
        document.getElementById('stat-shortlisted-count').textContent = m.shortlistedCount;
        document.getElementById('stat-shortlisted-sub').textContent = m.shortlistedCount;
        document.getElementById('stat-review-sub').textContent = m.reviewCount;
        document.getElementById('sidebar-job-count').textContent = m.activeJobs;
    },

    renderCharts(m) {
        // Score Distribution Bar Chart
        const ctxDist = document.getElementById('chart-score-dist');
        if (ctxDist) {
            if (app.charts.scoreDist) app.charts.scoreDist.destroy();

            app.charts.scoreDist = new Chart(ctxDist, {
                type: 'bar',
                data: {
                    labels: m.scoreDistribution.ranges,
                    datasets: [{
                        label: 'Candidates Count',
                        data: m.scoreDistribution.counts,
                        backgroundColor: ['#ef4444', '#f97316', '#f59e0b', '#3b82f6', '#10b981'],
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: { beginAtZero: true, ticks: { precision: 0 } },
                        x: { grid: { display: false } }
                    }
                }
            });
        }

        // Skills Frequency Chart
        const ctxSkills = document.getElementById('chart-skills-freq');
        if (ctxSkills) {
            if (app.charts.skillsFreq) app.charts.skillsFreq.destroy();

            const labels = m.topSkillsFrequency.map(s => s.skill);
            const data = m.topSkillsFrequency.map(s => s.count);

            app.charts.skillsFreq = new Chart(ctxSkills, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: data,
                        backgroundColor: [
                            '#4f46e5', '#06b6d4', '#10b981', '#f59e0b', '#8b5cf6',
                            '#ec4899', '#3b82f6', '#14b8a6', '#6366f1', '#f43f5e'
                        ],
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'right', labels: { boxWidth: 12, font: { size: 11 } } }
                    }
                }
            });
        }
    },

    renderTopCandidates(list) {
        const tbody = document.getElementById('dashboard-top-candidates-tbody');
        if (!list || list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No candidates scored yet.</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(c => {
            const scoreClass = c.totalScore >= 70 ? 'score-high' : (c.totalScore >= 45 ? 'score-mid' : 'score-low');
            const statusClass = c.status === 'SHORTLISTED' ? 'status-shortlisted' : (c.status === 'REVIEW' ? 'status-review' : 'status-rejected');

            return `
                <tr>
                    <td>
                        <div class="candidate-name-cell">
                            <strong>${c.candidateName || 'Candidate'}</strong>
                            <span>${c.email}</span>
                        </div>
                    </td>
                    <td><strong>${c.jobTitle}</strong></td>
                    <td>${c.experienceYears} Years</td>
                    <td><span class="badge badge-info">${c.educationLevel || 'Bachelors'}</span></td>
                    <td>
                        <div class="score-badge-circle ${scoreClass}">
                            ${c.totalScore.toFixed(0)}%
                        </div>
                    </td>
                    <td><span class="status-pill ${statusClass}">${c.status}</span></td>
                    <td>
                        <button class="btn btn-secondary btn-xs" onclick="screeningModule.viewCandidateDetail(${c.id})">
                            <i class="fa-solid fa-eye"></i> View Detail
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }
};

// ===================================================================
// 2. JOBS MODULE
// ===================================================================
const jobsModule = {
    jobsList: [],

    async load() {
        try {
            const res = await api.get('/jobs');
            if (res.success) {
                this.jobsList = res.data;
                app.allJobs = res.data;
                this.renderJobs(res.data);
            }
        } catch (err) {
            app.showToast('Failed to load jobs', 'error');
        }
    },

    handleSearch(query) {
        const q = query.toLowerCase().trim();
        const filtered = this.jobsList.filter(j => 
            j.title.toLowerCase().includes(q) || 
            (j.department && j.department.toLowerCase().includes(q)) ||
            (j.location && j.location.toLowerCase().includes(q))
        );
        this.renderJobs(filtered);
    },

    renderJobs(jobs) {
        const grid = document.getElementById('jobs-cards-grid');
        if (!jobs || jobs.length === 0) {
            grid.innerHTML = '<div class="card p-4 text-center text-muted" style="grid-column: 1/-1;">No job openings found. Click "Create Job Opening" to add one.</div>';
            return;
        }

        grid.innerHTML = jobs.map(j => {
            const reqSkills = (j.requiredSkills || []).map(s => `<span class="skill-tag skill-tag-req">${s}</span>`).join('');
            const prefSkills = (j.preferredSkills || []).map(s => `<span class="skill-tag">${s}</span>`).join('');

            return `
                <div class="job-card">
                    <div>
                        <div class="job-card-header">
                            <h3 class="job-card-title">${j.title}</h3>
                            <span class="badge ${j.active ? 'badge-primary' : 'badge-info'}">${j.active ? 'ACTIVE' : 'INACTIVE'}</span>
                        </div>
                        <div class="job-card-meta">
                            <span><i class="fa-solid fa-building"></i> ${j.department || 'General'}</span>
                            <span><i class="fa-solid fa-location-dot"></i> ${j.location || 'Remote'}</span>
                            <span><i class="fa-solid fa-briefcase"></i> ${j.minExperienceYears}+ Yrs</span>
                            <span><i class="fa-solid fa-graduation-cap"></i> ${j.educationRequirement}</span>
                        </div>
                        <p class="job-card-desc">${j.description}</p>
                        
                        <div class="mb-2"><strong class="text-xs text-muted">MANDATORY REQUIRED SKILLS:</strong></div>
                        <div class="skills-pill-group">${reqSkills || '<span class="text-xs text-muted">None specified</span>'}</div>

                        ${prefSkills ? `<div class="mb-2"><strong class="text-xs text-muted">PREFERRED SKILLS:</strong></div><div class="skills-pill-group">${prefSkills}</div>` : ''}
                    </div>

                    <div class="job-card-stats">
                        <span><i class="fa-solid fa-users"></i> ${j.totalApplicants} Candidates</span>
                        <span><i class="fa-solid fa-chart-line"></i> Avg: ${j.averageScore ? j.averageScore.toFixed(1) + '%' : 'N/A'}</span>
                        <div class="d-flex gap-1">
                            <button class="btn btn-outline btn-xs" onclick="jobsModule.editJob(${j.id})"><i class="fa-solid fa-pen"></i></button>
                            <button class="btn btn-outline btn-xs text-danger" onclick="jobsModule.deleteJob(${j.id})"><i class="fa-solid fa-trash"></i></button>
                            <button class="btn btn-primary btn-xs" onclick="screeningModule.selectJobForRanking(${j.id})"><i class="fa-solid fa-ranking-star"></i> Rank</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },

    async handleSaveJob(event) {
        event.preventDefault();
        const id = document.getElementById('job-id').value;
        const reqSkillsStr = document.getElementById('job-required-skills').value;
        const prefSkillsStr = document.getElementById('job-preferred-skills').value;

        const body = {
            title: document.getElementById('job-title').value.trim(),
            department: document.getElementById('job-department').value.trim(),
            location: document.getElementById('job-location').value.trim(),
            minExperienceYears: parseFloat(document.getElementById('job-experience').value) || 0,
            educationRequirement: document.getElementById('job-education').value,
            description: document.getElementById('job-description').value.trim(),
            requiredSkills: reqSkillsStr.split(',').map(s => s.trim()).filter(s => s.length > 0),
            preferredSkills: prefSkillsStr.split(',').map(s => s.trim()).filter(s => s.length > 0),
            active: document.getElementById('job-active').checked
        };

        try {
            if (id) {
                await api.put(`/jobs/${id}`, body);
                app.showToast('Job opening updated and candidates re-screened!');
            } else {
                await api.post('/jobs', body);
                app.showToast('Job opening created and screening initiated!');
            }
            app.closeModal('job-form-modal');
            this.load();
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    },

    editJob(id) {
        const job = this.jobsList.find(j => j.id === id);
        if (!job) return;

        document.getElementById('job-id').value = job.id;
        document.getElementById('job-title').value = job.title;
        document.getElementById('job-department').value = job.department || '';
        document.getElementById('job-location').value = job.location || '';
        document.getElementById('job-experience').value = job.minExperienceYears;
        document.getElementById('job-education').value = job.educationRequirement;
        document.getElementById('job-description').value = job.description;
        document.getElementById('job-required-skills').value = (job.requiredSkills || []).join(', ');
        document.getElementById('job-preferred-skills').value = (job.preferredSkills || []).join(', ');
        document.getElementById('job-active').checked = job.active;

        document.getElementById('job-modal-title').innerHTML = '<i class="fa-solid fa-pen-to-square"></i> Edit Job Opening';
        app.openModal('job-form-modal');
    },

    async deleteJob(id) {
        if (!confirm('Are you sure you want to delete this job opening and its associated screenings?')) return;
        try {
            await api.delete(`/jobs/${id}`);
            app.showToast('Job opening deleted successfully');
            this.load();
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    }
};

// ===================================================================
// 3. UPLOAD MODULE
// ===================================================================
const uploadModule = {
    selectedFiles: [],

    async load() {
        this.selectedFiles = [];
        this.updateQueueUI();
        this.setupDropZone();
        await this.populateJobsDropdown();
    },

    async populateJobsDropdown() {
        try {
            const res = await api.get('/jobs');
            if (res.success) {
                const select = document.getElementById('upload-target-job');
                select.innerHTML = '<option value="">-- Screen against all active jobs automatically --</option>' +
                    res.data.map(j => `<option value="${j.id}">${j.title} (${j.department || 'General'})</option>`).join('');
            }
        } catch (e) {}
    },

    setupDropZone() {
        const dropZone = document.getElementById('drop-zone');
        const fileInput = document.getElementById('file-input');

        dropZone.onclick = (e) => {
            if (e.target.tagName !== 'BUTTON') {
                fileInput.click();
            }
        };

        fileInput.onchange = (e) => {
            this.handleFiles(e.target.files);
        };

        dropZone.ondragover = (e) => {
            e.preventDefault();
            dropZone.classList.add('dragover');
        };

        dropZone.ondragleave = () => {
            dropZone.classList.remove('dragover');
        };

        dropZone.ondrop = (e) => {
            e.preventDefault();
            dropZone.classList.remove('dragover');
            this.handleFiles(e.dataTransfer.files);
        };
    },

    handleFiles(files) {
        for (let file of files) {
            const ext = file.name.split('.').pop().toLowerCase();
            if (!['pdf', 'docx', 'doc', 'txt'].includes(ext)) {
                app.showToast(`'${file.name}' has unsupported format. Only PDF/DOCX allowed.`, 'error');
                continue;
            }
            if (file.size > 10 * 1024 * 1024) {
                app.showToast(`'${file.name}' exceeds 10MB limit.`, 'error');
                continue;
            }
            this.selectedFiles.push(file);
        }
        this.updateQueueUI();
    },

    removeFile(index) {
        this.selectedFiles.splice(index, 1);
        this.updateQueueUI();
    },

    clearQueue() {
        this.selectedFiles = [];
        this.updateQueueUI();
    },

    updateQueueUI() {
        const container = document.getElementById('upload-queue-container');
        const list = document.getElementById('queue-items-list');
        const count = document.getElementById('queue-count');

        count.textContent = this.selectedFiles.length;
        if (this.selectedFiles.length === 0) {
            container.style.display = 'none';
            return;
        }

        container.style.display = 'block';
        list.innerHTML = this.selectedFiles.map((f, i) => `
            <div class="queue-item">
                <div class="d-flex align-center gap-2">
                    <i class="fa-solid fa-file-lines text-primary"></i>
                    <strong>${f.name}</strong>
                    <span class="text-xs text-muted">(${(f.size / 1024).toFixed(1)} KB)</span>
                </div>
                <button class="btn btn-outline btn-xs text-danger" onclick="uploadModule.removeFile(${i})">&times;</button>
            </div>
        `).join('');
    },

    async uploadAllFiles() {
        if (this.selectedFiles.length === 0) return;

        const btn = document.getElementById('btn-start-upload');
        btn.disabled = true;
        btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Parsing with Apache Tika & Scoring...';

        const formData = new FormData();
        this.selectedFiles.forEach(file => formData.append('files', file));
        formData.append('overwriteDuplicates', document.getElementById('overwrite-duplicates-check').checked);

        const targetJob = document.getElementById('upload-target-job').value;
        if (targetJob) formData.append('jobId', targetJob);

        try {
            const res = await api.post('/resumes/upload', formData);
            if (res.success) {
                app.showToast(`Uploaded & parsed ${res.data.totalUploaded} resumes successfully!`);
                if (res.data.errors && res.data.errors.length > 0) {
                    res.data.errors.forEach(err => app.showToast(err, 'error'));
                }
                this.clearQueue();
                // Switch to ranking view
                setTimeout(() => app.switchView('ranking'), 1000);
            }
        } catch (err) {
            app.showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<i class="fa-solid fa-bolt"></i> Start Parsing & Screening';
        }
    }
};

// ===================================================================
// 4. SCREENING & RANKING MODULE
// ===================================================================
const screeningModule = {
    currentJobId: null,
    candidates: [],
    selectedIds: new Set(),
    activeCandidateDetail: null,

    async load() {
        await this.loadJobsSelect();
    },

    async loadJobsSelect() {
        try {
            const res = await api.get('/jobs');
            if (res.success && res.data.length > 0) {
                const select = document.getElementById('ranking-job-select');
                select.innerHTML = res.data.map(j => `<option value="${j.id}">${j.title} (${j.totalApplicants} candidates)</option>`).join('');

                if (!this.currentJobId || !res.data.find(j => j.id == this.currentJobId)) {
                    this.currentJobId = res.data[0].id;
                }
                select.value = this.currentJobId;
                this.fetchCandidates();
            }
        } catch (e) {
            app.showToast('Failed to load jobs list', 'error');
        }
    },

    selectJobForRanking(jobId) {
        this.currentJobId = jobId;
        app.switchView('ranking');
    },

    handleJobChange(jobId) {
        this.currentJobId = jobId;
        this.selectedIds.clear();
        this.updateCompareButton();
        this.fetchCandidates();
    },

    async fetchCandidates() {
        if (!this.currentJobId) return;

        const tbody = document.getElementById('ranking-table-tbody');
        tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4"><i class="fa-solid fa-spinner fa-spin"></i> Screening and ranking candidates...</td></tr>';

        const q = document.getElementById('ranking-search-input').value;
        const minScore = document.getElementById('ranking-min-score').value;
        const status = document.getElementById('ranking-status-filter').value;
        const sortVal = document.getElementById('ranking-sort-by').value;
        const [sortBy, sortDir] = sortVal.split('_');

        let url = `/screening/jobs/${this.currentJobId}?sortBy=${sortBy}&sortDir=${sortDir}`;
        if (q) url += `&query=${encodeURIComponent(q)}`;
        if (minScore > 0) url += `&minScore=${minScore}`;
        if (status) url += `&status=${status}`;

        try {
            const res = await api.get(url);
            if (res.success) {
                this.candidates = res.data;
                document.getElementById('ranking-candidates-count').textContent = res.data.length;
                this.renderCandidatesTable(res.data);
            }
        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="9" class="text-center py-4 text-danger">${err.message}</td></tr>`;
        }
    },

    applyFilters() {
        this.fetchCandidates();
    },

    renderCandidatesTable(list) {
        const tbody = document.getElementById('ranking-table-tbody');
        if (!list || list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4 text-muted">No candidates match the specified filters.</td></tr>';
            return;
        }

        tbody.innerHTML = list.map((c, index) => {
            const scoreClass = c.totalScore >= 70 ? 'score-high' : (c.totalScore >= 45 ? 'score-mid' : 'score-low');
            const statusClass = c.status === 'SHORTLISTED' ? 'status-shortlisted' : (c.status === 'REVIEW' ? 'status-review' : 'status-rejected');
            const isChecked = this.selectedIds.has(c.id);

            const matchedSkillsCount = c.scoreBreakdown?.matchedRequiredSkills?.length || 0;
            const missingSkillsCount = c.scoreBreakdown?.missingRequiredSkills?.length || 0;

            return `
                <tr>
                    <td>
                        <input type="checkbox" class="candidate-check" value="${c.id}" ${isChecked ? 'checked' : ''} onchange="screeningModule.toggleCandidateSelect(${c.id}, this.checked)">
                    </td>
                    <td><strong>#${index + 1}</strong></td>
                    <td>
                        <div class="candidate-name-cell">
                            <strong>${c.candidateName || 'Candidate'}</strong>
                            <span>${c.email} • ${c.phone || 'No phone'}</span>
                        </div>
                    </td>
                    <td>
                        <div class="score-badge-circle ${scoreClass}">
                            ${c.totalScore.toFixed(0)}%
                        </div>
                    </td>
                    <td>
                        <span class="badge ${matchedSkillsCount > 0 ? 'badge-primary' : 'badge-info'}">
                            ${matchedSkillsCount} matched / ${matchedSkillsCount + missingSkillsCount} total
                        </span>
                    </td>
                    <td>${c.experienceYears} Yrs</td>
                    <td><span class="badge badge-info">${c.educationLevel || 'Bachelors'}</span></td>
                    <td>
                        <select class="form-control form-control-sm" style="width: 120px;" onchange="screeningModule.updateStatus(${c.id}, this.value)">
                            <option value="SHORTLISTED" ${c.status === 'SHORTLISTED' ? 'selected' : ''}>Shortlisted</option>
                            <option value="REVIEW" ${c.status === 'REVIEW' ? 'selected' : ''}>Review</option>
                            <option value="REJECTED" ${c.status === 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-secondary btn-xs" onclick="screeningModule.viewCandidateDetail(${c.id})">
                            <i class="fa-solid fa-eye"></i> Details
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    toggleCandidateSelect(id, checked) {
        if (checked) this.selectedIds.add(id);
        else this.selectedIds.delete(id);
        this.updateCompareButton();
    },

    toggleSelectAll(checked) {
        document.querySelectorAll('.candidate-check').forEach(cb => {
            cb.checked = checked;
            const id = parseInt(cb.value);
            if (checked) this.selectedIds.add(id);
            else this.selectedIds.delete(id);
        });
        this.updateCompareButton();
    },

    updateCompareButton() {
        const btn = document.getElementById('btn-compare-selected');
        const countSpan = document.getElementById('selected-compare-count');
        const count = this.selectedIds.size;
        countSpan.textContent = count;
        btn.disabled = count < 2 || count > 4;
    },

    compareSelected() {
        if (this.selectedIds.size < 2 || this.selectedIds.size > 4) {
            app.showToast('Please select between 2 and 4 candidates to compare', 'warning');
            return;
        }
        compareModule.screeningResultIdsToCompare = Array.from(this.selectedIds);
        app.switchView('compare');
    },

    async updateStatus(id, newStatus) {
        try {
            await api.patch(`/screening/${id}/status`, { status: newStatus });
            app.showToast(`Candidate status updated to ${newStatus}`);
            this.fetchCandidates();
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    },

    async rescreenJob() {
        if (!this.currentJobId) return;
        try {
            await api.post(`/jobs/${this.currentJobId}/rescreen`);
            app.showToast('Screening re-computed for all candidates!');
            this.fetchCandidates();
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    },

    exportCsv() {
        if (!this.currentJobId) return;
        const status = document.getElementById('ranking-status-filter').value;
        let url = `${API_BASE}/screening/jobs/${this.currentJobId}/export-csv`;
        if (status) url += `?status=${status}`;
        window.open(url, '_blank');
    },

    async viewCandidateDetail(id) {
        try {
            const res = await api.get(`/screening/${id}`);
            if (res.success && res.data) {
                this.activeCandidateDetail = res.data;
                this.renderDetailModal(res.data);
                app.openModal('candidate-detail-modal');
            }
        } catch (err) {
            app.showToast('Failed to load candidate details', 'error');
        }
    },

    renderDetailModal(c) {
        document.getElementById('modal-candidate-name').textContent = c.candidateName || 'Candidate';
        document.getElementById('modal-candidate-email').innerHTML = `<i class="fa-solid fa-envelope"></i> ${c.email}`;
        document.getElementById('modal-candidate-phone').innerHTML = `<i class="fa-solid fa-phone"></i> ${c.phone || 'Not Provided'}`;
        document.getElementById('modal-candidate-exp').innerHTML = `<i class="fa-solid fa-clock"></i> ${c.experienceYears} Years Exp`;
        document.getElementById('modal-candidate-edu').innerHTML = `<i class="fa-solid fa-graduation-cap"></i> ${c.educationLevel || 'Bachelors'}`;

        document.getElementById('modal-total-score-val').textContent = `${c.totalScore.toFixed(1)}%`;
        document.getElementById('modal-status-select').value = c.status;
        document.getElementById('modal-score-explanation').textContent = c.scoreExplanation;
        document.getElementById('modal-hr-notes-input').value = c.notes || '';

        const b = c.scoreBreakdown;
        if (b) {
            document.getElementById('wt-req').textContent = `${b.requiredSkillsWeight}%`;
            document.getElementById('val-req').textContent = `${b.requiredSkillsScore.toFixed(1)}%`;
            document.getElementById('bar-req').style.width = `${b.requiredSkillsScore}%`;

            document.getElementById('wt-pref').textContent = `${b.preferredSkillsWeight}%`;
            document.getElementById('val-pref').textContent = `${b.preferredSkillsScore.toFixed(1)}%`;
            document.getElementById('bar-pref').style.width = `${b.preferredSkillsScore}%`;

            document.getElementById('wt-sem').textContent = `${b.semanticSimilarityWeight}%`;
            document.getElementById('val-sem').textContent = `${b.semanticSimilarityScore.toFixed(1)}%`;
            document.getElementById('bar-sem').style.width = `${b.semanticSimilarityScore}%`;

            document.getElementById('wt-exp').textContent = `${b.experienceFitWeight}%`;
            document.getElementById('val-exp').textContent = `${b.experienceScore.toFixed(1)}%`;
            document.getElementById('bar-exp').style.width = `${b.experienceScore}%`;

            document.getElementById('wt-edu').textContent = `${b.educationFitWeight}%`;
            document.getElementById('val-edu').textContent = `${b.educationScore.toFixed(1)}%`;
            document.getElementById('bar-edu').style.width = `${b.educationScore}%`;

            // Render Radar Chart
            const ctx = document.getElementById('modal-radar-chart');
            if (ctx) {
                if (app.charts.detailRadar) app.charts.detailRadar.destroy();

                app.charts.detailRadar = new Chart(ctx, {
                    type: 'radar',
                    data: {
                        labels: ['Req Skills', 'Pref Skills', 'Semantic Fit', 'Experience', 'Education'],
                        datasets: [{
                            label: c.candidateName,
                            data: [
                                b.requiredSkillsScore,
                                b.preferredSkillsScore,
                                b.semanticSimilarityScore,
                                b.experienceScore,
                                b.educationScore
                            ],
                            backgroundColor: 'rgba(79, 70, 229, 0.2)',
                            borderColor: '#4f46e5',
                            pointBackgroundColor: '#4f46e5'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            r: { min: 0, max: 100, ticks: { stepSize: 25 } }
                        }
                    }
                });
            }

            // Skills Matrix
            const matchedReqEl = document.getElementById('modal-matched-req-skills');
            matchedReqEl.innerHTML = (b.matchedRequiredSkills && b.matchedRequiredSkills.length > 0)
                ? b.matchedRequiredSkills.map(s => `<span class="skill-tag skill-tag-match"><i class="fa-solid fa-check"></i> ${s}</span>`).join('')
                : '<span class="text-xs text-muted">No required skills matched</span>';

            const missingReqEl = document.getElementById('modal-missing-req-skills');
            missingReqEl.innerHTML = (b.missingRequiredSkills && b.missingRequiredSkills.length > 0)
                ? b.missingRequiredSkills.map(s => `<span class="skill-tag skill-tag-miss"><i class="fa-solid fa-xmark"></i> ${s}</span>`).join('')
                : '<span class="text-xs text-success">All required skills matched!</span>';

            const matchedPrefEl = document.getElementById('modal-matched-pref-skills');
            matchedPrefEl.innerHTML = (b.matchedPreferredSkills && b.matchedPreferredSkills.length > 0)
                ? b.matchedPreferredSkills.map(s => `<span class="skill-tag skill-tag-req"><i class="fa-solid fa-star"></i> ${s}</span>`).join('')
                : '<span class="text-xs text-muted">No preferred skills matched</span>';

            const allSkillsEl = document.getElementById('modal-all-parsed-skills');
            allSkillsEl.innerHTML = (c.parsedSkills && c.parsedSkills.length > 0)
                ? c.parsedSkills.map(s => `<span class="skill-tag">${s}</span>`).join('')
                : '<span class="text-xs text-muted">None detected</span>';
        }
    },

    async handleModalStatusChange(newStatus) {
        if (!this.activeCandidateDetail) return;
        await this.updateStatus(this.activeCandidateDetail.id, newStatus);
    },

    async saveModalNotes() {
        if (!this.activeCandidateDetail) return;
        const notes = document.getElementById('modal-hr-notes-input').value;
        try {
            await api.patch(`/screening/${this.activeCandidateDetail.id}/status`, {
                status: document.getElementById('modal-status-select').value,
                notes: notes
            });
            app.showToast('HR notes saved successfully');
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    },

    downloadOriginalResume() {
        if (!this.activeCandidateDetail) return;
        window.open(`${API_BASE}/resumes/${this.activeCandidateDetail.resumeId}/download`, '_blank');
    }
};

// ===================================================================
// 5. COMPARE CANDIDATES MODULE
// ===================================================================
const compareModule = {
    screeningResultIdsToCompare: [],

    async load() {
        await this.loadJobs();
    },

    async loadJobs() {
        try {
            const res = await api.get('/jobs');
            if (res.success && res.data.length > 0) {
                const select = document.getElementById('compare-job-select');
                select.innerHTML = res.data.map(j => `<option value="${j.id}">${j.title}</option>`).join('');

                const activeJobId = this.screeningResultIdsToCompare.length > 0 ? screeningModule.currentJobId : res.data[0].id;
                select.value = activeJobId;
                await this.loadCandidatesForJob(activeJobId);
            }
        } catch (e) {}
    },

    async loadCandidatesForJob(jobId) {
        try {
            const res = await api.get(`/screening/jobs/${jobId}`);
            if (res.success) {
                const container = document.getElementById('compare-candidate-checkboxes');
                container.innerHTML = res.data.map(c => `
                    <label class="form-check">
                        <input type="checkbox" class="form-check-input compare-chk" value="${c.id}" ${this.screeningResultIdsToCompare.includes(c.id) ? 'checked' : ''}>
                        <span>${c.candidateName} (${c.totalScore.toFixed(0)}%)</span>
                    </label>
                `).join('');

                if (this.screeningResultIdsToCompare.length >= 2) {
                    this.renderComparison();
                }
            }
        } catch (e) {}
    },

    async renderComparison() {
        const checked = Array.from(document.querySelectorAll('.compare-chk:checked')).map(cb => parseInt(cb.value));
        if (checked.length < 2 || checked.length > 4) {
            app.showToast('Please select 2 to 4 candidates to run side-by-side comparison', 'warning');
            return;
        }

        try {
            const res = await api.post('/screening/compare', { screeningResultIds: checked });
            if (res.success && res.data) {
                this.displayComparisonResults(res.data);
            }
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    },

    displayComparisonResults(data) {
        const container = document.getElementById('compare-results-container');
        container.style.display = 'block';

        const candidates = data.candidates;
        const colors = ['#4f46e5', '#06b6d4', '#10b981', '#f59e0b'];

        // 1. Radar Comparison
        const ctxRadar = document.getElementById('chart-compare-radar');
        if (ctxRadar) {
            if (app.charts.compareRadar) app.charts.compareRadar.destroy();

            const datasets = candidates.map((c, i) => ({
                label: c.candidateName,
                data: [
                    c.scoreBreakdown.requiredSkillsScore,
                    c.scoreBreakdown.preferredSkillsScore,
                    c.scoreBreakdown.semanticSimilarityScore,
                    c.scoreBreakdown.experienceScore,
                    c.scoreBreakdown.educationScore
                ],
                borderColor: colors[i % colors.length],
                backgroundColor: colors[i % colors.length] + '22',
                pointBackgroundColor: colors[i % colors.length]
            }));

            app.charts.compareRadar = new Chart(ctxRadar, {
                type: 'radar',
                data: {
                    labels: ['Required Skills', 'Preferred Skills', 'Semantic Fit', 'Experience', 'Education'],
                    datasets: datasets
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { r: { min: 0, max: 100 } }
                }
            });
        }

        // 2. Bar Comparison
        const ctxBar = document.getElementById('chart-compare-bar');
        if (ctxBar) {
            if (app.charts.compareBar) app.charts.compareBar.destroy();

            app.charts.compareBar = new Chart(ctxBar, {
                type: 'bar',
                data: {
                    labels: candidates.map(c => c.candidateName),
                    datasets: [{
                        label: 'Total Match Score (%)',
                        data: candidates.map(c => c.totalScore),
                        backgroundColor: colors.slice(0, candidates.length),
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { min: 0, max: 100 } }
                }
            });
        }

        // 3. Side by Side Cards
        const cardsGrid = document.getElementById('compare-cards-container');
        cardsGrid.innerHTML = candidates.map((c, i) => `
            <div class="card" style="border-top: 4px solid ${colors[i % colors.length]};">
                <div class="card-header">
                    <h4>${c.candidateName}</h4>
                    <span class="badge ${c.totalScore >= 70 ? 'badge-primary' : 'badge-info'}">${c.totalScore.toFixed(1)}% Match</span>
                </div>
                <div class="card-body">
                    <p class="text-xs text-muted mb-2"><i class="fa-solid fa-envelope"></i> ${c.email}</p>
                    <p class="text-xs text-muted mb-3"><i class="fa-solid fa-briefcase"></i> ${c.experienceYears} Years | ${c.educationLevel}</p>
                    
                    <strong class="text-xs">Required Skills Matched:</strong>
                    <div class="skills-pill-group mt-1">
                        ${(c.scoreBreakdown.matchedRequiredSkills || []).map(s => `<span class="skill-tag skill-tag-match">${s}</span>`).join('') || '<span class="text-xs text-muted">None</span>'}
                    </div>

                    <strong class="text-xs">Missing Required Skills:</strong>
                    <div class="skills-pill-group mt-1">
                        ${(c.scoreBreakdown.missingRequiredSkills || []).map(s => `<span class="skill-tag skill-tag-miss">${s}</span>`).join('') || '<span class="text-xs text-success">None</span>'}
                    </div>

                    <p class="text-xs text-muted mt-3">${c.scoreExplanation}</p>
                </div>
            </div>
        `).join('');
    }
};

// ===================================================================
// 6. SKILLS CATALOG MODULE
// ===================================================================
const skillsModule = {
    skills: [],
    categories: [],
    activeCategory: 'All',

    async load() {
        try {
            const res = await api.get('/skills');
            if (res.success && res.data) {
                this.skills = res.data;
                document.getElementById('skills-total-count').textContent = res.data.length;

                const cats = new Set(res.data.map(s => s.category).filter(Boolean));
                this.categories = ['All', ...Array.from(cats).sort()];
                this.renderCategories();
                this.renderSkills();
            }
        } catch (e) {
            app.showToast('Failed to load skills catalog', 'error');
        }
    },

    renderCategories() {
        const tabs = document.getElementById('skills-category-tabs');
        tabs.innerHTML = this.categories.map(c => `
            <button class="cat-tab ${c === this.activeCategory ? 'active' : ''}" onclick="skillsModule.setCategory('${c}')">${c}</button>
        `).join('');
    },

    setCategory(cat) {
        this.activeCategory = cat;
        this.renderCategories();
        this.renderSkills();
    },

    filterSkills(query) {
        const q = query.toLowerCase().trim();
        const filtered = this.skills.filter(s => {
            const inCat = this.activeCategory === 'All' || s.category === this.activeCategory;
            const matchesQ = s.name.toLowerCase().includes(q) || 
                             (s.synonyms && s.synonyms.some(syn => syn.toLowerCase().includes(q)));
            return inCat && matchesQ;
        });
        this.renderFilteredList(filtered);
    },

    renderSkills() {
        const filtered = this.activeCategory === 'All' 
            ? this.skills 
            : this.skills.filter(s => s.category === this.activeCategory);
        this.renderFilteredList(filtered);
    },

    renderFilteredList(list) {
        const container = document.getElementById('skills-tags-container');
        if (list.length === 0) {
            container.innerHTML = '<span class="text-muted text-sm p-3">No skills found.</span>';
            return;
        }

        container.innerHTML = list.map(s => `
            <div class="skill-catalog-badge" style="padding: 8px 12px; background: #ffffff; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 12px;">
                <strong>${s.name}</strong>
                <span class="text-xs text-muted d-block" style="font-size: 10px;">${s.category}</span>
            </div>
        `).join('');
    }
};

// ===================================================================
// 7. AUTH MODULE
// ===================================================================
const authModule = {
    async handleLogin(event) {
        event.preventDefault();
        const u = document.getElementById('login-username').value.trim();
        const p = document.getElementById('login-password').value.trim();

        try {
            const res = await api.post('/auth/login', { username: u, password: p });
            if (res.success && res.data) {
                app.currentUser = res.data;
                document.getElementById('user-display-name').textContent = res.data.fullName;
                document.getElementById('user-display-role').textContent = res.data.email;
                document.getElementById('user-avatar').textContent = res.data.fullName.substring(0, 2).toUpperCase();
                document.getElementById('btn-auth-toggle').innerHTML = '<i class="fa-solid fa-right-from-bracket"></i> Logout';
                app.closeModal('login-modal');
                app.showToast('Logged in successfully as ' + res.data.fullName);
            }
        } catch (err) {
            app.showToast(err.message, 'error');
        }
    }
};

// Bootstrap application on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    app.init();
});
